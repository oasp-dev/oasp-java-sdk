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
import dev.oasp.client.types.UnknownAuditEvent;
import java.time.Instant;
import java.util.Optional;

/**
 * Converts a single value to the generic tree {@link JsonWriter} understands:
 * a {@code switch} with pattern matching over every supported type, plus a
 * {@code default} that rejects anything else - explicit per-type mapping
 * rather than reflection, so every case is visible here and the compiler
 * catches a missing one.
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

            // Instant has no JSON representation of its own; ISO-8601 text
            // (via Instant.toString()) is the conventional, sortable choice.
            case Instant instant -> instant.toString();

            // Optional is never itself a JSON concept - only Conversation's
            // closedAt is Optional<Instant>. Empty writes as JSON null; a
            // present one writes as whatever its contents write as.
            case Optional<?> optional -> optional.isPresent() ? toTree(optional.get()) : null;

            // Enums write as their name(); reading is the strict direction
            // (see JsonFields.enumValue).
            case ScopeLevel level -> level.name();
            case ConversationState state -> state.name();

            case Principal principal -> TypeWriters.writePrincipal(principal);
            case ScopeClaim claim -> TypeWriters.writeScopeClaim(claim);
            case Conversation conversation -> TypeWriters.writeConversation(conversation);
            case CreateConversation createConversation ->
                    TypeWriters.writeCreateConversation(createConversation);
            case ErrorEnvelope errorEnvelope -> TypeWriters.writeErrorEnvelope(errorEnvelope);
            case ConversationCreated created -> TypeWriters.writeConversationCreated(created);
            case ConversationClosed closed -> TypeWriters.writeConversationClosed(closed);

            // Reached only when an UnknownAuditEvent shows up *nested* inside
            // some other value being written. The top-level, byte-for-byte
            // handling lives in HandRolledJsonCodec.write(); here we fall
            // back to re-parsing the preserved rawJson into a tree.
            case UnknownAuditEvent unknown -> JsonParser.parse(unknown.rawJson());

            default ->
                    throw new JsonException(
                            "Cannot write value of unsupported type: " + value.getClass().getName());
        };
    }
}
