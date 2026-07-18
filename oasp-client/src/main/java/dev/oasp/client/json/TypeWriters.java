package dev.oasp.client.json;

import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.ConversationClosed;
import dev.oasp.client.types.ConversationCreated;
import dev.oasp.client.types.CreateConversation;
import dev.oasp.client.types.ErrorEnvelope;
import dev.oasp.client.types.Principal;
import dev.oasp.client.types.ScopeClaim;
import java.util.LinkedHashMap;
import java.util.Map;

/** Record -> tree writers for each protocol type, dispatched to by {@link ProtocolTreeWriter}. */
final class TypeWriters {

    private TypeWriters() {}

    static Map<String, Object> writePrincipal(Principal principal) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("subject", principal.subject());
        tree.put("claims", principal.claims().stream().map(TypeWriters::writeScopeClaim).toList());
        return tree;
    }

    static Map<String, Object> writeScopeClaim(ScopeClaim claim) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("level", claim.level().name());
        tree.put("id", claim.id());
        return tree;
    }

    static Map<String, Object> writeConversation(Conversation conversation) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("id", conversation.id());
        tree.put("state", conversation.state().name());
        tree.put("principal", writePrincipal(conversation.principal()));
        tree.put("createdAt", conversation.createdAt().toString());
        // Optional.empty() -> JSON null, via the same Optional case in toTree.
        tree.put("closedAt", ProtocolTreeWriter.toTree(conversation.closedAt()));
        return tree;
    }

    static Map<String, Object> writeCreateConversation(CreateConversation createConversation) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("principal", writePrincipal(createConversation.principal()));
        return tree;
    }

    static Map<String, Object> writeErrorEnvelope(ErrorEnvelope errorEnvelope) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("code", errorEnvelope.code());
        tree.put("message", errorEnvelope.message());
        return tree;
    }

    static Map<String, Object> writeConversationCreated(ConversationCreated created) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("conversationId", created.conversationId());
        tree.put("occurredAt", created.occurredAt().toString());
        tree.put("actor", writePrincipal(created.actor()));
        // The discriminator that lets a future read() know which AuditEvent
        // subtype this is - see AuditEventTypes.
        tree.put("type", AuditEventTypes.CONVERSATION_CREATED);
        return tree;
    }

    static Map<String, Object> writeConversationClosed(ConversationClosed closed) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("conversationId", closed.conversationId());
        tree.put("occurredAt", closed.occurredAt().toString());
        tree.put("actor", writePrincipal(closed.actor()));
        tree.put("type", AuditEventTypes.CONVERSATION_CLOSED);
        return tree;
    }
}
