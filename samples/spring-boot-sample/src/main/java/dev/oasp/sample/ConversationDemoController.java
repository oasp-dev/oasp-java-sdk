package dev.oasp.sample;

import dev.oasp.client.OaspClient;
import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.CreateConversation;
import dev.oasp.client.types.Event;
import dev.oasp.client.types.PrincipalKind;
import dev.oasp.client.types.PrincipalRef;
import dev.oasp.client.types.Scope;
import dev.oasp.client.types.ScopeLevel;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demonstrates the injected {@link OaspClient} end-to-end.
 *
 * <p>The client here is the bean the starter auto-configured from {@code
 * oasp.*} — this controller never builds one itself; it just declares an
 * {@code OaspClient} constructor parameter and Spring hands it in.
 */
@RestController
public class ConversationDemoController {

    /** How many streamed events to collect before returning. */
    private static final int MAX_EVENTS = 5;

    private final OaspClient client;

    // Constructor injection: Spring passes the auto-configured OaspClient bean.
    ConversationDemoController(OaspClient client) {
        this.client = client;
    }

    /**
     * Runs the real flow against the configured OASP server: create a
     * conversation, send one message on its current session, then read back a
     * few streamed events.
     *
     * <p>Needs a reachable Loom at {@code oasp.base-url} to actually succeed;
     * the app itself starts fine without one because this only runs on request.
     */
    @PostMapping("/demo/converse")
    DemoResponse converse() {
        // In a real app these values come from the caller (the signed-in user,
        // the agent they picked, files to mount). Here they are illustrative.
        CreateConversation request = new CreateConversation(
                new Scope(ScopeLevel.WORKSPACE, "workspace-demo"),
                new PrincipalRef(PrincipalKind.USER, "user-demo"),
                "agent-demo",
                List.of());

        Conversation conversation = client.conversations().create(request);
        String sessionId = conversation.currentSessionId();

        client.sessions().send(sessionId, "Hello from the Spring Boot sample!");

        // stream() is a lazy, closeable Stream over Server-Sent Events; close it
        // (try-with-resources) so the connection is released after we take a few.
        List<EventView> events;
        try (Stream<Event> stream = client.sessions().stream(sessionId)) {
            events = stream.limit(MAX_EVENTS).map(EventView::from).toList();
        }

        return new DemoResponse(conversation.id(), sessionId, events);
    }

    /** The JSON returned to the caller. */
    record DemoResponse(String conversationId, String sessionId, List<EventView> events) {}
}
