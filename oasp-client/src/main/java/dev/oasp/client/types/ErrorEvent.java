package dev.oasp.client.types;

import java.time.Instant;
import java.util.Objects;

/**
 * An error condition encountered while executing the session.
 *
 * @param id          opaque, order-comparable event id (see {@link Event#id()})
 * @param at          when the event was emitted
 * @param message     human-readable description of the error
 * @param recoverable whether the session can continue after this error
 *                    (e.g. via drain) or is terminally failed
 */
public record ErrorEvent(String id, Instant at, String message, boolean recoverable) implements Event {

    public ErrorEvent {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(at, "at");
        Objects.requireNonNull(message, "message");
    }
}
