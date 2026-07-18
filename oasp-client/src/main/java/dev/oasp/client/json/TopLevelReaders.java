package dev.oasp.client.json;

import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.CreateConversation;
import dev.oasp.client.types.ErrorEnvelope;
import dev.oasp.client.types.Principal;
import dev.oasp.client.types.PrincipalKind;
import dev.oasp.client.types.Session;
import java.util.Map;

/** Tree -> record readers mirroring {@link TopLevelWriters}. */
final class TopLevelReaders {

    private TopLevelReaders() {}

    static Principal mapPrincipal(Map<String, Object> obj) {
        var id = JsonFields.string(obj, "id");
        var kind = JsonFields.enumValue(obj, "kind", PrincipalKind::fromWire);
        var identity = DatatypeReaders.mapPrincipalIdentity(JsonFields.object(obj, "identity"));
        var scopeMemberships = JsonFields.array(obj, "scopeMemberships").stream()
                .map(node -> DatatypeReaders.mapScope(JsonTrees.asObject(node, "scopeMemberships[]")))
                .toList();
        var roles = JsonFields.array(obj, "roles").stream()
                .map(node -> JsonTrees.asString(node, "roles[]"))
                .toList();
        return new Principal(id, kind, identity, scopeMemberships, roles);
    }

    static Conversation mapConversation(Map<String, Object> obj) {
        var id = JsonFields.string(obj, "id");
        var scope = DatatypeReaders.mapScope(JsonFields.object(obj, "scope"));
        var initiatingPrincipal = DatatypeReaders.mapPrincipalRef(JsonFields.object(obj, "initiatingPrincipal"));
        var currentSessionId = JsonFields.string(obj, "currentSessionId");
        var pinnedAgentVersion = DatatypeReaders.mapAgentVersionRef(JsonFields.object(obj, "pinnedAgentVersion"));
        var previousSessionIds = JsonFields.array(obj, "previousSessionIds").stream()
                .map(node -> JsonTrees.asString(node, "previousSessionIds[]"))
                .toList();
        return new Conversation(
                id, scope, initiatingPrincipal, currentSessionId, pinnedAgentVersion, previousSessionIds);
    }

    static Session mapSession(Map<String, Object> obj) {
        var id = JsonFields.string(obj, "id");
        var pinnedAgentVersion = DatatypeReaders.mapAgentVersionRef(JsonFields.object(obj, "pinnedAgentVersion"));
        var resources = JsonFields.array(obj, "resources").stream()
                .map(node -> SessionResourceReaders.mapSessionResource(JsonTrees.asObject(node, "resources[]")))
                .toList();
        var vaultIds = JsonFields.array(obj, "vaultIds").stream()
                .map(node -> JsonTrees.asString(node, "vaultIds[]"))
                .toList();
        return new Session(id, pinnedAgentVersion, resources, vaultIds);
    }

    static CreateConversation mapCreateConversation(Map<String, Object> obj) {
        var scope = DatatypeReaders.mapScope(JsonFields.object(obj, "scope"));
        var initiatingPrincipal = DatatypeReaders.mapPrincipalRef(JsonFields.object(obj, "initiatingPrincipal"));
        var definitionId = JsonFields.string(obj, "definitionId");
        var resources = JsonFields.array(obj, "resources").stream()
                .map(node -> SessionResourceReaders.mapSessionResource(JsonTrees.asObject(node, "resources[]")))
                .toList();
        return new CreateConversation(scope, initiatingPrincipal, definitionId, resources);
    }

    static ErrorEnvelope mapErrorEnvelope(Map<String, Object> obj) {
        var code = JsonFields.string(obj, "code");
        var message = JsonFields.string(obj, "message");
        return new ErrorEnvelope(code, message);
    }
}
