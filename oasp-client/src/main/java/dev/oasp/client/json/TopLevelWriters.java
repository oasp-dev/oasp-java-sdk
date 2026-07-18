package dev.oasp.client.json;

import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.CreateConversation;
import dev.oasp.client.types.ErrorEnvelope;
import dev.oasp.client.types.Principal;
import dev.oasp.client.types.Session;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Record -> tree writers for the top-level shapes with no discriminated
 * union of their own: the three "flat" resources ({@link Principal}, {@link
 * Conversation}, {@link Session}) and the two non-resource request/response
 * envelopes ({@link CreateConversation}, {@link ErrorEnvelope}). {@link
 * dev.oasp.client.types.AuditEvent} and {@link dev.oasp.client.types.Event}
 * are also top-level resources, but get their own {@code Audit*}/{@code
 * Event*} files - the former for its cluster of private embedded datatypes,
 * the latter because it's a discriminated union.
 */
final class TopLevelWriters {

    private TopLevelWriters() {}

    static Map<String, Object> writePrincipal(Principal principal) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("resourceType", principal.resourceType());
        tree.put("id", principal.id());
        tree.put("kind", principal.kind().wireValue());
        tree.put("identity", DatatypeWriters.writePrincipalIdentity(principal.identity()));
        tree.put(
                "scopeMemberships",
                principal.scopeMemberships().stream().map(DatatypeWriters::writeScope).toList());
        tree.put("roles", principal.roles());
        return tree;
    }

    static Map<String, Object> writeConversation(Conversation conversation) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("resourceType", conversation.resourceType());
        tree.put("id", conversation.id());
        tree.put("scope", DatatypeWriters.writeScope(conversation.scope()));
        tree.put("initiatingPrincipal", DatatypeWriters.writePrincipalRef(conversation.initiatingPrincipal()));
        tree.put("currentSessionId", conversation.currentSessionId());
        tree.put("pinnedAgentVersion", DatatypeWriters.writeAgentVersionRef(conversation.pinnedAgentVersion()));
        tree.put("previousSessionIds", conversation.previousSessionIds());
        return tree;
    }

    static Map<String, Object> writeSession(Session session) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("resourceType", session.resourceType());
        tree.put("id", session.id());
        tree.put("pinnedAgentVersion", DatatypeWriters.writeAgentVersionRef(session.pinnedAgentVersion()));
        tree.put("resources", session.resources().stream().map(SessionResourceWriters::write).toList());
        tree.put("vaultIds", session.vaultIds());
        return tree;
    }

    static Map<String, Object> writeCreateConversation(CreateConversation request) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("scope", DatatypeWriters.writeScope(request.scope()));
        tree.put("initiatingPrincipal", DatatypeWriters.writePrincipalRef(request.initiatingPrincipal()));
        tree.put("definitionId", request.definitionId());
        tree.put("resources", request.resources().stream().map(SessionResourceWriters::write).toList());
        return tree;
    }

    static Map<String, Object> writeErrorEnvelope(ErrorEnvelope envelope) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("code", envelope.code());
        tree.put("message", envelope.message());
        return tree;
    }
}
