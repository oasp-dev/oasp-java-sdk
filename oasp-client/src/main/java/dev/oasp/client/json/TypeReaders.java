package dev.oasp.client.json;

import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.ConversationClosed;
import dev.oasp.client.types.ConversationCreated;
import dev.oasp.client.types.ConversationState;
import dev.oasp.client.types.CreateConversation;
import dev.oasp.client.types.ErrorEnvelope;
import dev.oasp.client.types.Principal;
import dev.oasp.client.types.ScopeClaim;
import dev.oasp.client.types.ScopeLevel;
import java.util.Map;

/** Tree -> record readers for each protocol type, dispatched to by {@link ProtocolTreeReader}. */
final class TypeReaders {

    private TypeReaders() {}

    static Principal mapPrincipal(Map<String, Object> obj) {
        var claims = JsonFields.array(obj, "claims").stream()
                .map(node -> mapScopeClaim(JsonTrees.asObject(node, "claims[]")))
                .toList();
        return new Principal(JsonFields.string(obj, "subject"), claims);
    }

    static ScopeClaim mapScopeClaim(Map<String, Object> obj) {
        return new ScopeClaim(
                JsonFields.enumValue(ScopeLevel.class, obj, "level"), JsonFields.string(obj, "id"));
    }

    static Conversation mapConversation(Map<String, Object> obj) {
        return new Conversation(
                JsonFields.string(obj, "id"),
                JsonFields.enumValue(ConversationState.class, obj, "state"),
                mapPrincipal(JsonFields.object(obj, "principal")),
                JsonFields.instant(obj, "createdAt"),
                JsonFields.optionalInstant(obj, "closedAt"));
    }

    static CreateConversation mapCreateConversation(Map<String, Object> obj) {
        return new CreateConversation(mapPrincipal(JsonFields.object(obj, "principal")));
    }

    static ErrorEnvelope mapErrorEnvelope(Map<String, Object> obj) {
        return new ErrorEnvelope(JsonFields.string(obj, "code"), JsonFields.string(obj, "message"));
    }

    static ConversationCreated mapConversationCreated(Map<String, Object> obj) {
        return new ConversationCreated(
                JsonFields.string(obj, "conversationId"),
                JsonFields.instant(obj, "occurredAt"),
                mapPrincipal(JsonFields.object(obj, "actor")));
    }

    static ConversationClosed mapConversationClosed(Map<String, Object> obj) {
        return new ConversationClosed(
                JsonFields.string(obj, "conversationId"),
                JsonFields.instant(obj, "occurredAt"),
                mapPrincipal(JsonFields.object(obj, "actor")));
    }
}
