package dev.oasp.client.types;

import java.time.Instant;
import java.util.Objects;

/**
 * The fallback {@link Event}: used when a server emits an event whose
 * {@code type} discriminator this SDK version does not recognise.
 *
 * <p>The common envelope - {@code id}, {@code at} - is shared by every event
 * variant, recognised or not, so even an unrecognised event still carries
 * those normally. Only the event's {@code type} and any type-specific data
 * are opaque to this SDK version; those are preserved verbatim as {@link
 * #type()} and {@link #rawJson()} instead of discarded.
 *
 * @param id      opaque, order-comparable event id (see {@link Event#id()})
 * @param at      when the event was emitted
 * @param type    the raw event-type discriminator the server sent
 * @param rawJson the event's raw JSON, preserved verbatim
 */
public record UnknownEvent(String id, Instant at, String type, String rawJson) implements Event {

    public UnknownEvent {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(at, "at");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(rawJson, "rawJson");
    }
}
