package dev.oasp.client.types;

import java.time.Instant;
import java.util.Objects;

/**
 * Marks the start of a new assistant message.
 *
 * @param id        opaque, order-comparable event id (see {@link Event#id()})
 * @param at        when the event was emitted
 * @param messageId identifier of the assistant message beginning
 */
public record AssistantMessageStartEvent(String id, Instant at, String messageId) implements Event {

    public AssistantMessageStartEvent {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(at, "at");
        Objects.requireNonNull(messageId, "messageId");
    }
}
