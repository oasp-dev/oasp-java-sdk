package dev.oasp.client.types;

import java.time.Instant;
import java.util.Objects;

/**
 * An incremental text chunk of an in-progress assistant message.
 *
 * @param id        opaque, order-comparable event id (see {@link Event#id()})
 * @param at        when the event was emitted
 * @param messageId identifier of the assistant message this chunk belongs to
 * @param delta     the incremental text content of this chunk
 */
public record AssistantMessageTextEvent(String id, Instant at, String messageId, String delta) implements Event {

    public AssistantMessageTextEvent {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(at, "at");
        Objects.requireNonNull(messageId, "messageId");
        Objects.requireNonNull(delta, "delta");
    }
}
