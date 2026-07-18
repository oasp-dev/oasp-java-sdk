package dev.oasp.client.json;

import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.ConversationState;
import dev.oasp.client.types.CreateConversation;
import dev.oasp.client.types.ErrorEnvelope;
import dev.oasp.client.types.Principal;
import dev.oasp.client.types.ScopeClaim;
import dev.oasp.client.types.ScopeLevel;
import java.util.Map;

/** Tree -> record readers for the non-event protocol types, dispatched to by {@link ProtocolTreeReader}. */
final class TypeReaders {

    private TypeReaders() {}

    static Principal mapPrincipal(Map<String, Object> obj) {
        var subject = JsonFields.string(obj, "subject");
        var claims = JsonFields.array(obj, "claims").stream()
                .map(node -> mapScopeClaim(JsonTrees.asObject(node, "claims[]")))
                .toList();
        return new Principal(subject, claims);
    }

    static ScopeClaim mapScopeClaim(Map<String, Object> obj) {
        var level = JsonFields.enumValue(ScopeLevel.class, obj, "level");
        var id = JsonFields.string(obj, "id");
        return new ScopeClaim(level, id);
    }

    static Conversation mapConversation(Map<String, Object> obj) {
        var id = JsonFields.string(obj, "id");
        var state = JsonFields.enumValue(ConversationState.class, obj, "state");
        var principal = mapPrincipal(JsonFields.object(obj, "principal"));
        var createdAt = JsonFields.instant(obj, "createdAt");
        var closedAt = JsonFields.optionalInstant(obj, "closedAt");
        return new Conversation(id, state, principal, createdAt, closedAt);
    }

    static CreateConversation mapCreateConversation(Map<String, Object> obj) {
        var principal = mapPrincipal(JsonFields.object(obj, "principal"));
        return new CreateConversation(principal);
    }

    static ErrorEnvelope mapErrorEnvelope(Map<String, Object> obj) {
        var code = JsonFields.string(obj, "code");
        var message = JsonFields.string(obj, "message");
        return new ErrorEnvelope(code, message);
    }
}
