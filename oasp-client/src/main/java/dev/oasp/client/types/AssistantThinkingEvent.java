package dev.oasp.client.types;

import java.time.Instant;
import java.util.Objects;

/**
 * An incremental chunk of the assistant's extended thinking.
 *
 * @param id    opaque, order-comparable event id (see {@link Event#id()})
 * @param at    when the event was emitted
 * @param delta the incremental thinking content of this chunk
 */
public record AssistantThinkingEvent(String id, Instant at, String delta) implements Event {

    public AssistantThinkingEvent {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(at, "at");
        Objects.requireNonNull(delta, "delta");
    }
}
