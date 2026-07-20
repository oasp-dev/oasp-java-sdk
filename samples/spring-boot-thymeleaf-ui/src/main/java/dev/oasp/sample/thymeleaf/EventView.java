package dev.oasp.sample.thymeleaf;

import dev.oasp.client.types.AssistantMessageEndEvent;
import dev.oasp.client.types.AssistantMessageStartEvent;
import dev.oasp.client.types.AssistantMessageTextEvent;
import dev.oasp.client.types.Event;
import dev.oasp.client.types.StatusEvent;

/**
 * A small, template-friendly view of a streamed {@link Event}.
 *
 * <p>{@code Event} is a sealed interface with many record variants. Rather
 * than teach the HTML template about every variant, we flatten each event
 * into three plain strings the page can print directly: the event kind, its
 * id, and a short human-readable {@code detail} (the text chunk, a status,
 * etc.). This keeps the template simple for someone new to the SDK.
 *
 * @param kind   the event variant's simple class name, e.g. {@code AssistantMessageTextEvent}
 * @param id     the event's opaque, order-comparable id
 * @param detail a short human-readable summary of the event's payload
 */
public record EventView(String kind, String id, String detail) {

    /**
     * Flattens an {@link Event} into a view. The {@code switch} pattern-matches
     * on the sealed type: each case pulls out the field that matters for that
     * variant. The {@code default} covers the variants this demo does not
     * surface (thinking, tool-use, error, unknown).
     */
    static EventView from(Event event) {
        String detail =
                switch (event) {
                    case AssistantMessageTextEvent e -> e.delta();
                    case AssistantMessageStartEvent e -> "message " + e.messageId() + " started";
                    case AssistantMessageEndEvent e -> "message " + e.messageId() + " ended";
                    case StatusEvent e -> "status: " + e.status().wireValue();
                    default -> "";
                };
        return new EventView(event.getClass().getSimpleName(), event.id(), detail);
    }
}
