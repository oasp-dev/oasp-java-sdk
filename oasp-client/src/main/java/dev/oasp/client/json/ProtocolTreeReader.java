package dev.oasp.client.json;

import dev.oasp.client.types.AgentVersionRef;
import dev.oasp.client.types.AuditEvent;
import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.CreateConversation;
import dev.oasp.client.types.ErrorEnvelope;
import dev.oasp.client.types.Event;
import dev.oasp.client.types.Principal;
import dev.oasp.client.types.PrincipalIdentity;
import dev.oasp.client.types.PrincipalRef;
import dev.oasp.client.types.Resource;
import dev.oasp.client.types.Scope;
import dev.oasp.client.types.Session;
import dev.oasp.client.types.SessionResource;
import dev.oasp.client.types.UnknownResource;
import java.util.Map;

/**
 * Maps a parsed JSON tree onto the requested {@code type}. One {@code if}
 * per supported type, mirroring the {@code switch} in {@link
 * ProtocolTreeWriter#toTree(Object)}. {@code originalJson} is threaded
 * through only where a fallback record needs the raw source text verbatim -
 * {@link Resource}/{@link UnknownResource} and {@code Event}/{@code
 * UnknownEvent} - since those are the only fallbacks read here, at the top
 * level, where the full original document text is still available.
 */
final class ProtocolTreeReader {

    private ProtocolTreeReader() {}

    static Object fromTree(Object node, Class<?> type, String originalJson) {
        if (type == Resource.class) {
            return ResourceDispatch.mapResource(JsonTrees.asObject(node, "root"), originalJson);
        }
        if (type == Principal.class) {
            return TopLevelReaders.mapPrincipal(JsonTrees.asObject(node, "root"));
        }
        if (type == Conversation.class) {
            return TopLevelReaders.mapConversation(JsonTrees.asObject(node, "root"));
        }
        if (type == Session.class) {
            return TopLevelReaders.mapSession(JsonTrees.asObject(node, "root"));
        }
        if (type == AuditEvent.class) {
            return AuditReaders.mapAuditEvent(JsonTrees.asObject(node, "root"));
        }
        if (type == Event.class) {
            return EventReaders.mapEvent(JsonTrees.asObject(node, "root"), originalJson);
        }
        if (type == CreateConversation.class) {
            return TopLevelReaders.mapCreateConversation(JsonTrees.asObject(node, "root"));
        }
        if (type == ErrorEnvelope.class) {
            return TopLevelReaders.mapErrorEnvelope(JsonTrees.asObject(node, "root"));
        }
        if (type == SessionResource.class) {
            return SessionResourceReaders.mapSessionResource(JsonTrees.asObject(node, "root"));
        }
        if (type == Scope.class) {
            return DatatypeReaders.mapScope(JsonTrees.asObject(node, "root"));
        }
        if (type == PrincipalRef.class) {
            return DatatypeReaders.mapPrincipalRef(JsonTrees.asObject(node, "root"));
        }
        if (type == AgentVersionRef.class) {
            return DatatypeReaders.mapAgentVersionRef(JsonTrees.asObject(node, "root"));
        }
        if (type == PrincipalIdentity.class) {
            return DatatypeReaders.mapPrincipalIdentity(JsonTrees.asObject(node, "root"));
        }
        if (type == UnknownResource.class) {
            Map<String, Object> root = JsonTrees.asObject(node, "root");
            return new UnknownResource(JsonFields.string(root, "resourceType"), originalJson);
        }
        throw new JsonException("Cannot read JSON as unsupported type: " + type.getName());
    }
}
