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
        // Every supported top-level type is a JSON object, so coerce once here.
        Map<String, Object> root = JsonTrees.asObject(node, "root");
        if (type == Resource.class) {
            return ResourceDispatch.mapResource(root, originalJson);
        }
        if (type == Principal.class) {
            return TopLevelReaders.mapPrincipal(root);
        }
        if (type == Conversation.class) {
            return TopLevelReaders.mapConversation(root);
        }
        if (type == Session.class) {
            return TopLevelReaders.mapSession(root);
        }
        if (type == AuditEvent.class) {
            return AuditReaders.mapAuditEvent(root);
        }
        if (type == Event.class) {
            return EventReaders.mapEvent(root, originalJson);
        }
        if (type == CreateConversation.class) {
            return TopLevelReaders.mapCreateConversation(root);
        }
        if (type == ErrorEnvelope.class) {
            return TopLevelReaders.mapErrorEnvelope(root);
        }
        if (type == SessionResource.class) {
            return SessionResourceReaders.mapSessionResource(root);
        }
        if (type == Scope.class) {
            return DatatypeReaders.mapScope(root);
        }
        if (type == PrincipalRef.class) {
            return DatatypeReaders.mapPrincipalRef(root);
        }
        if (type == AgentVersionRef.class) {
            return DatatypeReaders.mapAgentVersionRef(root);
        }
        if (type == PrincipalIdentity.class) {
            return DatatypeReaders.mapPrincipalIdentity(root);
        }
        if (type == UnknownResource.class) {
            return new UnknownResource(JsonFields.string(root, "resourceType"), originalJson);
        }
        throw new JsonException("Cannot read JSON as unsupported type: " + type.getName());
    }
}
