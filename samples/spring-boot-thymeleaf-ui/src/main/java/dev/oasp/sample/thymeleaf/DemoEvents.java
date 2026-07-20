package dev.oasp.sample.thymeleaf;

import dev.oasp.client.types.AssistantMessageEndEvent;
import dev.oasp.client.types.AssistantMessageStartEvent;
import dev.oasp.client.types.AssistantMessageTextEvent;
import dev.oasp.client.types.Event;
import dev.oasp.client.types.SessionRunStatus;
import dev.oasp.client.types.StatusEvent;
import java.time.Instant;
import java.util.List;

/**
 * Produces a scripted list of {@link Event}s for OFFLINE DEMO MODE.
 *
 * <p>When {@code oasp.demo=true} the controller renders these instead of
 * calling a real OASP server, so the whole page is demonstrable with no Loom
 * running and no network. The events are the same real SDK types the live
 * stream would emit — a message start, a few text chunks, a message end, then
 * a final idle status — so the page looks exactly as it would against a
 * server, just built by hand here.
 */
final class DemoEvents {

    private DemoEvents() {}

    /** A canned assistant reply that echoes the user's message. */
    static List<Event> scriptedReply(String userMessage) {
        Instant now = Instant.now();
        String messageId = "demo-message-1";
        return List.of(
                new AssistantMessageStartEvent("evt-1", now, messageId),
                new AssistantMessageTextEvent("evt-2", now, messageId, "You said: \"" + userMessage + "\". "),
                new AssistantMessageTextEvent("evt-3", now, messageId, "This reply is scripted "),
                new AssistantMessageTextEvent("evt-4", now, messageId, "because oasp.demo=true."),
                new AssistantMessageEndEvent("evt-5", now, messageId),
                new StatusEvent("evt-6", now, SessionRunStatus.IDLE));
    }
}
