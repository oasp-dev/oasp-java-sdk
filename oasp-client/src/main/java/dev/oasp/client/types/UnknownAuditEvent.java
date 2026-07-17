package dev.oasp.client.types;

import java.time.Instant;
import java.util.Objects;

/**
 * The fallback {@link AuditEvent}: used when a server reports an event whose
 * {@code type} discriminator this SDK version does not recognise.
 *
 * <p>The common envelope - {@code conversationId}, {@code occurredAt},
 * {@code actor} - is shared by every audit event, recognised or not, so even
 * an unrecognised event still carries those three fields normally. Only the
 * event's {@code type} and any type-specific data are actually opaque to
 * this SDK version; those are preserved verbatim as {@link #type()} and
 * {@link #rawJson()} rather than discarded, so a newer server can introduce
 * event types without older SDK versions losing information or failing to
 * deserialize the event at all.
 *
 * <p>{@code rawJson} is a plain {@code String} rather than a parsed tree
 * because this module has no JSON library (see the zero-runtime-dependency
 * rule on {@code oasp-client}); callers who need structure out of it are
 * expected to parse it themselves.
 *
 * <p>Note: this type only models the fallback shape. Actually recognising an
 * unknown {@code type} and routing it here instead of throwing is done by
 * the JSON deserialization layer, not by this record.
 *
 * @param conversationId the id of the conversation the event occurred on
 * @param occurredAt     when the event occurred
 * @param actor          the principal that caused the event
 * @param type           the raw event-type discriminator the server sent,
 *                       e.g. {@code "conversation.archived"}
 * @param rawJson        the event's raw JSON, preserved verbatim
 */
public record UnknownAuditEvent(
        String conversationId, Instant occurredAt, Principal actor, String type, String rawJson)
        implements AuditEvent {

    public UnknownAuditEvent {
        // Same lenient stance on conversationId as the other AuditEvent
        // implementations: non-null only, since it's inbound server data.
        Objects.requireNonNull(conversationId, "conversationId");
        Objects.requireNonNull(occurredAt, "occurredAt");
        Objects.requireNonNull(actor, "actor");

        // type is what makes this event "unknown" in the first place; a
        // blank value would carry no information about what was actually
        // received, so - unlike conversationId - it must be non-blank too.
        Objects.requireNonNull(type, "type");
        if (type.isBlank()) {
            throw new IllegalArgumentException("type must not be blank");
        }

        Objects.requireNonNull(rawJson, "rawJson");
    }
}
