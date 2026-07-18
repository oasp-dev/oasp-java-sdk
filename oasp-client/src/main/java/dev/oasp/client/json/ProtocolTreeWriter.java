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
import dev.oasp.client.types.Scope;
import dev.oasp.client.types.Session;
import dev.oasp.client.types.SessionResource;
import dev.oasp.client.types.UnknownResource;
import java.util.Map;
import java.util.Optional;

/**
 * Converts a single value to the generic tree {@link JsonWriter} understands:
 * a {@code switch} with pattern matching over every supported top-level
 * type, dispatching to the small per-domain writer classes that build each
 * type's tree. Those writers recurse into each other directly rather than
 * back through here - this switch is only the entry point {@link
 * HandRolledJsonCodec#write(Object)} calls.
 */
final class ProtocolTreeWriter {

    private ProtocolTreeWriter() {}

    static Object toTree(Object value) {
        return switch (value) {
            case null -> null;

            // Values JsonWriter already understands natively pass through unchanged.
            case String s -> s;
            case Boolean b -> b;
            case Long l -> l;
            case Double d -> d;

            case Principal principal -> TopLevelWriters.writePrincipal(principal);
            case Conversation conversation -> TopLevelWriters.writeConversation(conversation);
            case Session session -> TopLevelWriters.writeSession(session);
            case AuditEvent auditEvent -> AuditWriters.writeAuditEvent(auditEvent);
            case Event event -> EventWriters.write(event);
            case CreateConversation createConversation -> TopLevelWriters.writeCreateConversation(createConversation);
            case ErrorEnvelope errorEnvelope -> TopLevelWriters.writeErrorEnvelope(errorEnvelope);
            case SessionResource sessionResource -> SessionResourceWriters.write(sessionResource);

            // The embedded datatypes are normally only ever reached via the
            // resource writers that embed them - these cases exist so a
            // datatype can also be written/read as its own top-level value
            // (e.g. in tests), not just nested.
            case Scope scope -> DatatypeWriters.writeScope(scope);
            case PrincipalRef principalRef -> DatatypeWriters.writePrincipalRef(principalRef);
            case AgentVersionRef agentVersionRef -> DatatypeWriters.writeAgentVersionRef(agentVersionRef);
            case PrincipalIdentity principalIdentity -> DatatypeWriters.writePrincipalIdentity(principalIdentity);

            // Reached only if an UnknownResource shows up nested inside some
            // other value being written - nothing currently embeds a bare
            // Resource, so in practice this is only ever the top-level
            // write() target, which HandRolledJsonCodec special-cases for
            // byte-for-byte fidelity before this switch ever runs.
            case UnknownResource unknown -> JsonParser.parse(unknown.rawJson());

            default ->
                    throw new JsonException(
                            "Cannot write value of unsupported type: " + value.getClass().getName());
        };
    }

    /**
     * Adds {@code name -> value} only when {@code value} is present; an
     * absent {@link Optional} field is omitted from the tree entirely
     * (never written as JSON {@code null}) - every vendored schema declares
     * its optional properties as plain, non-nullable types, so omission is
     * what a spec-conformant payload looks like.
     */
    static void putIfPresent(Map<String, Object> tree, String name, Optional<?> value) {
        value.ifPresent(v -> tree.put(name, v));
    }
}
