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
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Tree -> record readers for each protocol type, dispatched to by {@link ProtocolTreeReader}. */
final class TypeReaders {

    private TypeReaders() {}

    static Principal mapPrincipal(Map<String, Object> obj) {
        String subject = JsonTrees.asString(JsonTrees.field(obj, "subject"), "subject");
        List<Object> claimsNode = JsonTrees.asArray(JsonTrees.field(obj, "claims"), "claims");
        List<ScopeClaim> claims =
                claimsNode.stream().map(node -> mapScopeClaim(JsonTrees.asObject(node, "claims[]"))).toList();
        return new Principal(subject, claims);
    }

    static ScopeClaim mapScopeClaim(Map<String, Object> obj) {
        ScopeLevel level =
                ValueMappers.mapEnum(ScopeLevel.class, JsonTrees.asString(JsonTrees.field(obj, "level"), "level"));
        String id = JsonTrees.asString(JsonTrees.field(obj, "id"), "id");
        return new ScopeClaim(level, id);
    }

    static Conversation mapConversation(Map<String, Object> obj) {
        String id = JsonTrees.asString(JsonTrees.field(obj, "id"), "id");
        ConversationState state =
                ValueMappers.mapEnum(
                        ConversationState.class, JsonTrees.asString(JsonTrees.field(obj, "state"), "state"));
        Principal principal = mapPrincipal(JsonTrees.asObject(JsonTrees.field(obj, "principal"), "principal"));
        Instant createdAt =
                ValueMappers.mapInstant(JsonTrees.asString(JsonTrees.field(obj, "createdAt"), "createdAt"));
        Optional<Instant> closedAt = ValueMappers.mapOptionalInstant(JsonTrees.field(obj, "closedAt"));
        return new Conversation(id, state, principal, createdAt, closedAt);
    }

    static CreateConversation mapCreateConversation(Map<String, Object> obj) {
        Principal principal = mapPrincipal(JsonTrees.asObject(JsonTrees.field(obj, "principal"), "principal"));
        return new CreateConversation(principal);
    }

    static ErrorEnvelope mapErrorEnvelope(Map<String, Object> obj) {
        String code = JsonTrees.asString(JsonTrees.field(obj, "code"), "code");
        String message = JsonTrees.asString(JsonTrees.field(obj, "message"), "message");
        return new ErrorEnvelope(code, message);
    }

    static ConversationCreated mapConversationCreated(Map<String, Object> obj) {
        return new ConversationCreated(
                JsonTrees.asString(JsonTrees.field(obj, "conversationId"), "conversationId"),
                ValueMappers.mapInstant(JsonTrees.asString(JsonTrees.field(obj, "occurredAt"), "occurredAt")),
                mapPrincipal(JsonTrees.asObject(JsonTrees.field(obj, "actor"), "actor")));
    }

    static ConversationClosed mapConversationClosed(Map<String, Object> obj) {
        return new ConversationClosed(
                JsonTrees.asString(JsonTrees.field(obj, "conversationId"), "conversationId"),
                ValueMappers.mapInstant(JsonTrees.asString(JsonTrees.field(obj, "occurredAt"), "occurredAt")),
                mapPrincipal(JsonTrees.asObject(JsonTrees.field(obj, "actor"), "actor")));
    }
}
