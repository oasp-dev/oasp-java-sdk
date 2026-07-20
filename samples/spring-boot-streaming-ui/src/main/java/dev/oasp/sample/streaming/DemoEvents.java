package dev.oasp.sample.streaming;

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
 * <p>When {@code oasp.demo=true} the controller emits these one-by-one (with a
 * small delay between each) instead of calling a real OASP server, so the live
 * token-by-token streaming effect is fully demonstrable with no Loom running
 * and no network. The events are the exact same SDK types a real stream would
 * emit — a message start, several text chunks, a message end, then a final idle
 * status — just built by hand here, so the browser cannot tell the difference.
 */
final class DemoEvents {

    private DemoEvents() {}

    /** A canned assistant reply, split into several chunks to show streaming. */
    static List<Event> scriptedReply(String userMessage) {
        Instant now = Instant.now();
        String messageId = "demo-message-1";
        return List.of(
                new AssistantMessageStartEvent("evt-1", now, messageId),
                new AssistantMessageTextEvent("evt-2", now, messageId, "You said: “" + userMessage + "”. "),
                new AssistantMessageTextEvent("evt-3", now, messageId, "This reply is streamed "),
                new AssistantMessageTextEvent("evt-4", now, messageId, "one chunk at a time "),
                new AssistantMessageTextEvent("evt-5", now, messageId, "because oasp.demo=true. "),
                new AssistantMessageTextEvent("evt-6", now, messageId, "Set it to false to talk to a real server."),
                new AssistantMessageEndEvent("evt-7", now, messageId),
                new StatusEvent("evt-8", now, SessionRunStatus.IDLE));
    }
}
