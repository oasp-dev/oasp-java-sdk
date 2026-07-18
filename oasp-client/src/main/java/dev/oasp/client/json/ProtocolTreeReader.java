package dev.oasp.client.json;

import dev.oasp.client.types.AuditEvent;
import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.ConversationClosed;
import dev.oasp.client.types.ConversationCreated;
import dev.oasp.client.types.ConversationState;
import dev.oasp.client.types.CreateConversation;
import dev.oasp.client.types.ErrorEnvelope;
import dev.oasp.client.types.Principal;
import dev.oasp.client.types.ScopeClaim;
import dev.oasp.client.types.ScopeLevel;
import dev.oasp.client.types.UnknownAuditEvent;
import java.util.Map;

/**
 * Maps a parsed JSON tree onto the requested {@code type}. One {@code if}
 * per supported type, mirroring the {@code switch} in {@link
 * ProtocolTreeWriter#toTree(Object)}; {@code originalJson} is threaded
 * through only for {@link AuditEvent}/{@link UnknownAuditEvent}, which need
 * the raw source text to populate {@code rawJson()}.
 */
final class ProtocolTreeReader {

    private ProtocolTreeReader() {}

    static Object fromTree(Object node, Class<?> type, String originalJson) {
        if (type == Principal.class) {
            return TypeReaders.mapPrincipal(JsonTrees.asObject(node, "root"));
        }
        if (type == ScopeClaim.class) {
            return TypeReaders.mapScopeClaim(JsonTrees.asObject(node, "root"));
        }
        if (type == ScopeLevel.class) {
            return ValueMappers.mapEnum(ScopeLevel.class, JsonTrees.asString(node, "root"));
        }
        if (type == ConversationState.class) {
            return ValueMappers.mapEnum(ConversationState.class, JsonTrees.asString(node, "root"));
        }
        if (type == Conversation.class) {
            return TypeReaders.mapConversation(JsonTrees.asObject(node, "root"));
        }
        if (type == CreateConversation.class) {
            return TypeReaders.mapCreateConversation(JsonTrees.asObject(node, "root"));
        }
        if (type == ErrorEnvelope.class) {
            return TypeReaders.mapErrorEnvelope(JsonTrees.asObject(node, "root"));
        }
        if (type == AuditEvent.class) {
            return AuditEventMapper.mapAuditEvent(JsonTrees.asObject(node, "root"), originalJson);
        }
        if (type == ConversationCreated.class) {
            return TypeReaders.mapConversationCreated(JsonTrees.asObject(node, "root"));
        }
        if (type == ConversationClosed.class) {
            return TypeReaders.mapConversationClosed(JsonTrees.asObject(node, "root"));
        }
        if (type == UnknownAuditEvent.class) {
            Map<String, Object> root = JsonTrees.asObject(node, "root");
            return AuditEventMapper.mapUnknownAuditEvent(
                    root, JsonFields.string(root, "type"), originalJson);
        }
        throw new JsonException("Cannot read JSON as unsupported type: " + type.getName());
    }
}
