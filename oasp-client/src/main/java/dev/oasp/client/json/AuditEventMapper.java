package dev.oasp.client.json;

import dev.oasp.client.types.AuditEvent;
import dev.oasp.client.types.ConversationClosed;
import dev.oasp.client.types.ConversationCreated;
import dev.oasp.client.types.UnknownAuditEvent;
import java.util.Map;

/**
 * Dispatches a parsed {@link AuditEvent} object on its {@code type}
 * discriminator, and holds the concrete event-record readers. A recognised
 * value maps to the matching record; an unrecognised value - the fallback
 * deferred from issue #4 - maps to {@link UnknownAuditEvent} instead of
 * throwing. See {@link AuditEventTypes} for the (assumed) discriminator strings.
 */
final class AuditEventMapper {

    private AuditEventMapper() {}

    static AuditEvent mapAuditEvent(Map<String, Object> obj, String originalJson) {
        var discriminator = JsonFields.string(obj, "type");
        return switch (discriminator) {
            case AuditEventTypes.CONVERSATION_CREATED -> mapConversationCreated(obj);
            case AuditEventTypes.CONVERSATION_CLOSED -> mapConversationClosed(obj);
            default -> mapUnknownAuditEvent(obj, discriminator, originalJson);
        };
    }

    static ConversationCreated mapConversationCreated(Map<String, Object> obj) {
        var conversationId = JsonFields.string(obj, "conversationId");
        var occurredAt = JsonFields.instant(obj, "occurredAt");
        var actor = TypeReaders.mapPrincipal(JsonFields.object(obj, "actor"));
        return new ConversationCreated(conversationId, occurredAt, actor);
    }

    static ConversationClosed mapConversationClosed(Map<String, Object> obj) {
        var conversationId = JsonFields.string(obj, "conversationId");
        var occurredAt = JsonFields.instant(obj, "occurredAt");
        var actor = TypeReaders.mapPrincipal(JsonFields.object(obj, "actor"));
        return new ConversationClosed(conversationId, occurredAt, actor);
    }

    static UnknownAuditEvent mapUnknownAuditEvent(
            Map<String, Object> obj, String discriminator, String originalJson) {
        var conversationId = JsonFields.string(obj, "conversationId");
        var occurredAt = JsonFields.instant(obj, "occurredAt");
        var actor = TypeReaders.mapPrincipal(JsonFields.object(obj, "actor"));
        // originalJson is the exact text passed to read(), kept verbatim as
        // rawJson so nothing about the unrecognised event is lost and write()
        // can later re-emit it byte-for-byte.
        return new UnknownAuditEvent(conversationId, occurredAt, actor, discriminator, originalJson);
    }
}
