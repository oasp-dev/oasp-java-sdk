package dev.oasp.sample.streaming;

import dev.oasp.client.OaspClient;
import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.CreateConversation;
import dev.oasp.client.types.Event;
import dev.oasp.client.types.PrincipalKind;
import dev.oasp.client.types.PrincipalRef;
import dev.oasp.client.types.Scope;
import dev.oasp.client.types.ScopeLevel;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Runs one conversation turn on a background thread and pushes every OASP
 * {@link Event} onto an {@link SseEmitter} as a JSON {@link EventPayload}, so
 * the browser sees each chunk arrive live. This is the heart of the sample: the
 * bridge from the SDK's {@code Stream<Event>} to Server-Sent Events.
 */
final class ConversationStream implements Runnable {

    /** Delay between scripted demo chunks, so the browser visibly streams. */
    private static final long DEMO_CHUNK_DELAY_MS = 220;
    /** How many live events to forward before completing (keeps the demo short). */
    private static final int MAX_EVENTS = 20;

    private final OaspClient client;
    private final boolean demo;
    private final String message;
    private final SseEmitter emitter;

    ConversationStream(OaspClient client, boolean demo, String message, SseEmitter emitter) {
        this.client = client;
        this.demo = demo;
        this.message = message;
        this.emitter = emitter;
    }

    @Override
    public void run() {
        try {
            if (demo) {
                runDemo();
            } else {
                runReal();
            }
            // Tell the browser the turn is over. The client listens for this named
            // "done" event and closes the EventSource, which stops SSE from
            // auto-reconnecting once we complete the emitter below.
            emitter.send(SseEmitter.event().name("done").data("end"));
            emitter.complete();
        } catch (IOException disconnected) {
            // Browser closed the tab / navigated away mid-stream: nothing to do.
            emitter.complete();
        } catch (RuntimeException failure) {
            emitter.completeWithError(failure);
        }
    }

    /** OFFLINE DEMO MODE: emit scripted events with a delay so streaming shows. */
    private void runDemo() throws IOException {
        for (Event event : DemoEvents.scriptedReply(message)) {
            emit(event);
            sleep(DEMO_CHUNK_DELAY_MS);
        }
    }

    /** LIVE MODE: drive the real injected client (create → send → stream). */
    private void runReal() throws IOException {
        CreateConversation request = new CreateConversation(
                new Scope(ScopeLevel.WORKSPACE, "workspace-demo"),
                new PrincipalRef(PrincipalKind.USER, "user-demo"),
                "agent-demo",
                List.of());
        Conversation conversation = client.conversations().create(request);
        String sessionId = conversation.currentSessionId();
        client.sessions().send(sessionId, message);

        // stream() is a lazy, closeable Stream over Server-Sent Events; iterate it
        // and forward each event as it arrives, then close it (try-with-resources).
        try (Stream<Event> stream = client.sessions().stream(sessionId)) {
            for (Event event : (Iterable<Event>) stream.limit(MAX_EVENTS)::iterator) {
                emit(event);
            }
        }
    }

    private void emit(Event event) throws IOException {
        emitter.send(EventPayload.from(event));
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
