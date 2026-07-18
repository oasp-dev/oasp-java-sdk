package dev.oasp.client;

import dev.oasp.client.http.OaspHttpTransport;
import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.CreateConversation;
import java.util.Objects;

/**
 * The Conversation-lifecycle API group. Reached via {@link
 * OaspClient#conversations()}.
 */
public final class Conversations {

    private final OaspHttpTransport transport;

    Conversations(OaspHttpTransport transport) {
        this.transport = transport;
    }

    /**
     * {@code createConversation}: {@code POST /conversations} -> {@code 201}
     * with the new {@link Conversation}, riding on its freshly minted initial
     * Session.
     *
     * <p>The request body follows {@link CreateConversation}, which is this
     * SDK's provisional inference - oasp-standard still marks the operation a
     * placeholder (see {@link CreateConversation}'s own Javadoc).
     */
    public Conversation create(CreateConversation request) {
        Objects.requireNonNull(request, "request");
        return transport.post("/conversations", request, Conversation.class);
    }

    // migrateConversation (POST /conversations/{id}/migrate) is intentionally
    // omitted for now: interactions.md requires the caller to name a target
    // agent version, but the v1alpha1 OpenAPI publishes neither a request body
    // nor parameters for it, so its input shape is genuinely unspecified.
    // Deferred until oasp-standard pins the contract, rather than invented.
}
