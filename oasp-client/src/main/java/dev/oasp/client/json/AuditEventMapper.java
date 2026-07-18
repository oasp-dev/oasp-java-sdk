package dev.oasp.client.json;

import dev.oasp.client.types.AuditEvent;
import dev.oasp.client.types.Principal;
import dev.oasp.client.types.UnknownAuditEvent;
import java.util.Map;

/**
 * Dispatches a parsed {@link AuditEvent} object on its {@code type}
 * discriminator: a recognised value maps to the matching record, and - the
 * fallback behaviour deferred from issue #4 - an unrecognised value maps to
 * {@link UnknownAuditEvent} instead of throwing. See {@link AuditEventTypes}
 * for the (assumed) discriminator strings.
 */
final class AuditEventMapper {

    private AuditEventMapper() {}

    static AuditEvent mapAuditEvent(Map<String, Object> obj, String originalJson) {
        String discriminator = JsonFields.string(obj, "type");
        return switch (discriminator) {
            case AuditEventTypes.CONVERSATION_CREATED -> TypeReaders.mapConversationCreated(obj);
            case AuditEventTypes.CONVERSATION_CLOSED -> TypeReaders.mapConversationClosed(obj);
            default -> mapUnknownAuditEvent(obj, discriminator, originalJson);
        };
    }

    static UnknownAuditEvent mapUnknownAuditEvent(
            Map<String, Object> obj, String discriminator, String originalJson) {
        Principal actor = TypeReaders.mapPrincipal(JsonFields.object(obj, "actor"));
        // originalJson is the exact text passed to read(), preserved
        // verbatim as rawJson so nothing about the unrecognised event is
        // lost and write() can later re-emit it byte-for-byte.
        return new UnknownAuditEvent(
                JsonFields.string(obj, "conversationId"),
                JsonFields.instant(obj, "occurredAt"),
                actor,
                discriminator,
                originalJson);
    }
}
