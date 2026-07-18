package dev.oasp.client;

import dev.oasp.client.http.OaspHttpTransport;
import dev.oasp.client.http.TokenProvider;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Objects;

/**
 * The entry point consumers use to talk to an OASP server. Immutable and
 * thread-safe once built; construct one with {@link #builder()} and share it.
 *
 * <p>The API is grouped to mirror the protocol surface: {@link
 * #conversations()} for the Conversation lifecycle and {@link #sessions()}
 * for a live Session's message/stream traffic.
 */
public final class OaspClient {

    private final Conversations conversations;
    private final Sessions sessions;

    private OaspClient(OaspHttpTransport transport) {
        this.conversations = new Conversations(transport);
        this.sessions = new Sessions(transport);
    }

    /** Conversation-lifecycle calls: {@code create}, {@code migrate}. */
    public Conversations conversations() {
        return conversations;
    }

    /** Live-Session calls: {@code send} a turn, {@code stream} its events, {@code drain} it. */
    public Sessions sessions() {
        return sessions;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Collects and validates the client's configuration. {@code baseUrl} and
     * {@code tokenProvider} are required; the two timeouts default to 10s if
     * left unset.
     */
    public static final class Builder {

        private URI baseUrl;
        private TokenProvider tokenProvider;
        private Duration connectTimeout = Duration.ofSeconds(10);
        private Duration requestTimeout = Duration.ofSeconds(10);

        private Builder() {}

        /** The OASP server root, e.g. {@code https://api.example.com}. Request paths are resolved against it. */
        public Builder baseUrl(URI baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /** Supplies the bearer token sent on every request; consulted once per request. */
        public Builder tokenProvider(TokenProvider tokenProvider) {
            this.tokenProvider = tokenProvider;
            return this;
        }

        /** How long to wait establishing a TCP connection before giving up. */
        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = Objects.requireNonNull(connectTimeout, "connectTimeout");
            return this;
        }

        /** How long to wait for a response once the request is sent before giving up. */
        public Builder requestTimeout(Duration requestTimeout) {
            this.requestTimeout = Objects.requireNonNull(requestTimeout, "requestTimeout");
            return this;
        }

        public OaspClient build() {
            Objects.requireNonNull(baseUrl, "baseUrl is required");
            Objects.requireNonNull(tokenProvider, "tokenProvider is required");

            var httpClient = HttpClient.newBuilder().connectTimeout(connectTimeout).build();
            return new OaspClient(new OaspHttpTransport(httpClient, baseUrl, tokenProvider, requestTimeout));
        }
    }
}
