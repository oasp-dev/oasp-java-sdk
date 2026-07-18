package dev.oasp.client.json;

import dev.oasp.client.types.AgentVersionRef;
import dev.oasp.client.types.PrincipalIdentity;
import dev.oasp.client.types.PrincipalKind;
import dev.oasp.client.types.PrincipalRef;
import dev.oasp.client.types.Scope;
import dev.oasp.client.types.ScopeLevel;
import java.util.Map;

/** Tree -> record readers mirroring {@link DatatypeWriters}. */
final class DatatypeReaders {

    private DatatypeReaders() {}

    static Scope mapScope(Map<String, Object> obj) {
        var level = JsonFields.enumValue(obj, "level", ScopeLevel::fromWire);
        var id = JsonFields.string(obj, "id");
        return new Scope(level, id);
    }

    static PrincipalRef mapPrincipalRef(Map<String, Object> obj) {
        var kind = JsonFields.enumValue(obj, "kind", PrincipalKind::fromWire);
        var id = JsonFields.string(obj, "id");
        return new PrincipalRef(kind, id);
    }

    static AgentVersionRef mapAgentVersionRef(Map<String, Object> obj) {
        var agentDefinitionId = JsonFields.string(obj, "agentDefinitionId");
        var version = JsonFields.longValue(obj, "version");
        return new AgentVersionRef(agentDefinitionId, version);
    }

    static PrincipalIdentity mapPrincipalIdentity(Map<String, Object> obj) {
        var subject = JsonFields.string(obj, "subject");
        var issuer = JsonFields.optionalString(obj, "issuer");
        var displayName = JsonFields.optionalString(obj, "displayName");
        var email = JsonFields.optionalString(obj, "email");
        return new PrincipalIdentity(subject, issuer, displayName, email);
    }
}
