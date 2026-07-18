package dev.oasp.client.json;

import dev.oasp.client.types.AgentVersionRef;
import dev.oasp.client.types.PrincipalIdentity;
import dev.oasp.client.types.PrincipalRef;
import dev.oasp.client.types.Scope;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Record -> tree writers for the embedded <em>datatypes</em> shared across
 * resources: {@link Scope}, {@link PrincipalRef}, {@link AgentVersionRef},
 * and {@link PrincipalIdentity}. None of these carry {@code resourceType} -
 * per their Javadoc, each is mapped by field position and dispatched to by
 * the field name that embeds it, never by a discriminator of its own.
 */
final class DatatypeWriters {

    private DatatypeWriters() {}

    static Map<String, Object> writeScope(Scope scope) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("level", scope.level().wireValue());
        tree.put("id", scope.id());
        return tree;
    }

    static Map<String, Object> writePrincipalRef(PrincipalRef ref) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("kind", ref.kind().wireValue());
        tree.put("id", ref.id());
        return tree;
    }

    static Map<String, Object> writeAgentVersionRef(AgentVersionRef ref) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("agentDefinitionId", ref.agentDefinitionId());
        tree.put("version", ref.version());
        return tree;
    }

    static Map<String, Object> writePrincipalIdentity(PrincipalIdentity identity) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("subject", identity.subject());
        ProtocolTreeWriter.putIfPresent(tree, "issuer", identity.issuer());
        ProtocolTreeWriter.putIfPresent(tree, "displayName", identity.displayName());
        ProtocolTreeWriter.putIfPresent(tree, "email", identity.email());
        return tree;
    }
}
