package dev.oasp.client.types;

import java.time.Instant;
import java.util.Objects;

/**
 * An {@link AuditEvent} reported when a {@link Conversation} is created.
 *
 * @param conversationId the id of the conversation that was created
 * @param occurredAt     when the creation occurred
 * @param actor          the principal that created the conversation
 */
public record ConversationCreated(String conversationId, Instant occurredAt, Principal actor)
        implements AuditEvent {

    public ConversationCreated {
        // Only non-null, no non-blank check on conversationId: this is an
        // inbound server event, and elsewhere in this package (e.g.
        // Conversation.id) we validate exactly what the spec calls for on
        // server-assigned/server-reported data, not more.
        Objects.requireNonNull(conversationId, "conversationId");
        Objects.requireNonNull(occurredAt, "occurredAt");
        Objects.requireNonNull(actor, "actor");
    }
}
