package dev.oasp.sample.streaming;

import dev.oasp.client.OaspClient;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * The SSE endpoint the single-page app talks to. Returning an {@link SseEmitter}
 * keeps the HTTP response open; we then push events onto it from a background
 * thread, and the browser's {@code EventSource} renders each one as it arrives.
 *
 * <p>{@code @RestController}: the returned {@code SseEmitter} is the streaming
 * response body, not a view name.
 */
@RestController
public class ConverseController {

    /** Small thread pool: each in-flight conversation runs on one borrowed thread. */
    private final ExecutorService workers = Executors.newCachedThreadPool();

    private final OaspClient client;
    private final boolean demo;

    /**
     * Constructor injection. Spring hands in the auto-configured
     * {@link OaspClient} bean, plus the {@code oasp.demo} flag (default
     * {@code true}) that decides whether we stream scripted events or drive
     * the real client.
     */
    ConverseController(OaspClient client, @Value("${oasp.demo:true}") boolean demo) {
        this.client = client;
        this.demo = demo;
    }

    /**
     * GET /api/converse?message=... — open a Server-Sent Events stream for one
     * conversation turn. The flow runs on a background thread so this method
     * returns immediately with the still-open emitter; events are pushed as
     * they are produced.
     */
    @GetMapping("/api/converse")
    SseEmitter converse(@RequestParam("message") String message) {
        // 0 = no server-side timeout; the turn completes on its own or when the
        // browser disconnects. Fine for a local demo.
        SseEmitter emitter = new SseEmitter(0L);
        workers.execute(new ConversationStream(client, demo, message, emitter));
        return emitter;
    }

    /** Stop accepting work and let in-flight threads finish as the app shuts down. */
    @PreDestroy
    void shutdown() {
        workers.shutdown();
    }
}
