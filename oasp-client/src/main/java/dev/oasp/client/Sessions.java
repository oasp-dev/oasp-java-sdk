package dev.oasp.client;

import dev.oasp.client.http.OaspHttpTransport;
import dev.oasp.client.types.Event;
import dev.oasp.client.types.Session;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The live-Session API group: post a turn, stream its normalised events, or
 * drain it. Reached via {@link OaspClient#sessions()}.
 */
public final class Sessions {

    private final OaspHttpTransport transport;

    Sessions(OaspHttpTransport transport) {
        this.transport = transport;
    }

    /**
     * {@code send}: {@code POST /sessions/{id}/messages} -> {@code 202}
     * Accepted (no body). Posts {@code message} as a new turn attributed to
     * the calling principal.
     *
     * <p>Returns {@code void} because the server answers {@code 202} with no
     * body. The request body is provisional: v1alpha1 marks {@code sendMessage}
     * a placeholder with no documented message schema, so this SDK sends
     * {@code {"content": <message>}} - see {@link MessageBodies}.
     */
    public void send(String sessionId, String message) {
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(message, "message");
        transport.postAccepted("/sessions/" + sessionId + "/messages", MessageBodies.of(message));
    }

    /**
     * {@code stream}: {@code GET /sessions/{id}/events} -> a lazy, closeable
     * {@link Stream} of {@link Event}s over Server-Sent Events. Close the
     * stream (try-with-resources) to release the connection if you stop early.
     */
    public Stream<Event> stream(String sessionId) {
        Objects.requireNonNull(sessionId, "sessionId");
        return transport.stream("/sessions/" + sessionId + "/events");
    }

    /**
     * {@code drain}: {@code POST /sessions/{id}/drain} -> {@code 200} with the
     * {@link Session}, returned to idle. No request body is defined for this
     * operation, so none is sent.
     */
    public Session drain(String sessionId) {
        Objects.requireNonNull(sessionId, "sessionId");
        return transport.post("/sessions/" + sessionId + "/drain", Session.class);
    }
}
