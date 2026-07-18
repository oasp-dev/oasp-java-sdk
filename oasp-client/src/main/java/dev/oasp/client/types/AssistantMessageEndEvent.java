package dev.oasp.client.types;

import java.time.Instant;
import java.util.Objects;

/**
 * Marks the end of an assistant message.
 *
 * @param id        opaque, order-comparable event id (see {@link Event#id()})
 * @param at        when the event was emitted
 * @param messageId identifier of the assistant message ending
 */
public record AssistantMessageEndEvent(String id, Instant at, String messageId) implements Event {

    public AssistantMessageEndEvent {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(at, "at");
        Objects.requireNonNull(messageId, "messageId");
    }
}
