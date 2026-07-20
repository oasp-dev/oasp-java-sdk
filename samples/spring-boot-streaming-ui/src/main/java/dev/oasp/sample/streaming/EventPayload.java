package dev.oasp.sample.streaming;

import dev.oasp.client.types.AssistantMessageEndEvent;
import dev.oasp.client.types.AssistantMessageStartEvent;
import dev.oasp.client.types.AssistantMessageTextEvent;
import dev.oasp.client.types.AssistantThinkingEvent;
import dev.oasp.client.types.ErrorEvent;
import dev.oasp.client.types.Event;
import dev.oasp.client.types.StatusEvent;

/**
 * The tiny JSON shape we send to the browser for each streamed {@link Event}.
 *
 * <p>{@code Event} is a sealed interface with many record variants. Rather than
 * teach the JavaScript about every Java type, we flatten each event into a flat
 * bag the browser can {@code switch} on: a {@code kind} discriminator plus a few
 * optional fields. Spring serialises this record to JSON automatically, and
 * (with {@code non_null} inclusion set in application.yaml) the null fields are
 * simply omitted, so a text chunk is just {@code {"kind":"text","text":"..."}}.
 *
 * @param kind      short discriminator the frontend switches on (e.g. "text")
 * @param text      chunk / message text, when the variant carries one
 * @param status    session status wire value, for a status event
 * @param messageId id of the assistant message, for message start/end/text
 */
public record EventPayload(String kind, String text, String status, String messageId) {

    /** Flattens a live SDK {@link Event} into the browser-facing payload. */
    static EventPayload from(Event event) {
        return switch (event) {
            case AssistantMessageStartEvent e -> new EventPayload("message-start", null, null, e.messageId());
            case AssistantMessageTextEvent e -> new EventPayload("text", e.delta(), null, e.messageId());
            case AssistantMessageEndEvent e -> new EventPayload("message-end", null, null, e.messageId());
            case AssistantThinkingEvent e -> new EventPayload("thinking", e.delta(), null, null);
            case StatusEvent e -> new EventPayload("status", null, e.status().wireValue(), null);
            case ErrorEvent e -> new EventPayload("error", e.message(), null, null);
            // Tool-use / unknown variants this demo does not surface individually.
            default -> new EventPayload(event.getClass().getSimpleName(), null, null, null);
        };
    }
}
