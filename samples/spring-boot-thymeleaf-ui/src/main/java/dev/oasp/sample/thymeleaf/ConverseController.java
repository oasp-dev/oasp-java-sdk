package dev.oasp.sample.thymeleaf;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A classic server-rendered controller: each request runs the flow on the
 * server and returns a fully-rendered HTML page. There is no live streaming
 * to the browser — the server collects the events, then renders them in one
 * go. (The separate streaming sample shows events arriving live.)
 *
 * <p>{@code @Controller} (not {@code @RestController}): the strings returned
 * from the mapping methods are Thymeleaf template names, not response bodies.
 */
@Controller
public class ConverseController {

    /** How many streamed events to collect before rendering the page. */
    private static final int MAX_EVENTS = 10;

    private final OaspClient client;
    private final boolean demo;

    /**
     * Constructor injection. Spring hands in the auto-configured
     * {@link OaspClient} bean, plus the {@code oasp.demo} flag (default
     * {@code true}) that decides whether we call the real client or render
     * scripted events.
     */
    ConverseController(OaspClient client, @Value("${oasp.demo:true}") boolean demo) {
        this.client = client;
        this.demo = demo;
    }

    /** GET / — show the form with the message input and submit button. */
    @GetMapping("/")
    String index(Model model) {
        model.addAttribute("demo", demo);
        return "index";
    }

    /**
     * POST /converse — the form submit. Runs the conversation flow on the
     * server and renders the result page. In demo mode the events are
     * scripted; otherwise they come from a real OASP server.
     */
    @PostMapping("/converse")
    String converse(@RequestParam("message") String message, Model model) {
        ConverseResult result = demo ? runDemo(message) : runReal(message);
        model.addAttribute("result", result);
        return "result";
    }

    /** OFFLINE DEMO MODE: no client call — hand back scripted events. */
    private ConverseResult runDemo(String message) {
        List<EventView> events =
                DemoEvents.scriptedReply(message).stream().map(EventView::from).toList();
        return new ConverseResult(message, "demo-conversation", "demo-session", true, events);
    }

    /** LIVE MODE: drive the real injected client against the configured server. */
    private ConverseResult runReal(String message) {
        // In a real app these values come from the caller (the signed-in user,
        // the agent they picked, files to mount). Here they are illustrative,
        // and the resources list is left empty.
        CreateConversation request = new CreateConversation(
                new Scope(ScopeLevel.WORKSPACE, "workspace-demo"),
                new PrincipalRef(PrincipalKind.USER, "user-demo"),
                "agent-demo",
                List.of());

        Conversation conversation = client.conversations().create(request);
        String sessionId = conversation.currentSessionId();
        client.sessions().send(sessionId, message);

        // stream() is a lazy, closeable Stream over Server-Sent Events; close it
        // (try-with-resources) so the connection is released after we take a few.
        List<EventView> events;
        try (Stream<Event> stream = client.sessions().stream(sessionId)) {
            events = stream.limit(MAX_EVENTS).map(EventView::from).toList();
        }
        return new ConverseResult(message, conversation.id(), sessionId, false, events);
    }
}
