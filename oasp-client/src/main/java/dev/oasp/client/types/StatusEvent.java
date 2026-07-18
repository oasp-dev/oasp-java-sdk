package dev.oasp.client.types;

import java.time.Instant;
import java.util.Objects;

/**
 * A transition in the session's status.
 *
 * @param id     opaque, order-comparable event id (see {@link Event#id()})
 * @param at     when the event was emitted
 * @param status the session's new status
 */
public record StatusEvent(String id, Instant at, SessionRunStatus status) implements Event {

    public StatusEvent {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(at, "at");
        Objects.requireNonNull(status, "status");
    }
}
