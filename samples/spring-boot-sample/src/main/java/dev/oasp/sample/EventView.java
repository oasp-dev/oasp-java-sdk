package dev.oasp.sample;

import dev.oasp.client.types.Event;

/**
 * A small, JSON-friendly view of a streamed {@link Event}.
 *
 * <p>{@code Event} is a sealed interface with many record variants; rather
 * than expose every field of every variant, the demo returns just the event
 * kind (the record's simple class name, e.g. {@code AssistantMessageTextEvent})
 * plus the two fields common to all events. That keeps the response readable
 * for someone new to the SDK.
 *
 * @param kind the event variant's simple class name
 * @param id   the event's opaque, order-comparable id
 * @param at   ISO-8601 timestamp of when the event was emitted
 */
public record EventView(String kind, String id, String at) {

    static EventView from(Event event) {
        return new EventView(event.getClass().getSimpleName(), event.id(), event.at().toString());
    }
}
