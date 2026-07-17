package dev.oasp.client.types;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * A conversation as reported by an OASP server: its id, lifecycle state, the
 * principal it was opened for, and its timestamps.
 *
 * @param id        the server-assigned conversation id
 * @param state     the current lifecycle state
 * @param principal the principal the conversation was opened on behalf of
 * @param createdAt when the conversation was created
 * @param closedAt  when the conversation was closed, if it has been
 */
public record Conversation(
        String id,
        ConversationState state,
        Principal principal,
        Instant createdAt,

        // A record accessor's return type must match its component's type exactly,
        // so a component typed `Instant` could never have an `Optional<Instant>`
        // accessor - that would be two methods with the same signature but
        // different return types, which doesn't compile. Typing the component
        // itself as Optional<Instant> is the only way to get the Optional-typed
        // accessor the API calls for. The tradeoff, tolerated below, is that the
        // compact constructor must accept a plain `null` (e.g. from a
        // deserializer that doesn't know about Optional) and normalize it to
        // Optional.empty(), since Optional itself offers no null-safe way in.
        Optional<Instant> closedAt) {

    public Conversation {
        // Only a non-null check for id, not blank: the spec for this type calls
        // for non-null on id/state/principal/createdAt only (unlike, say,
        // Principal.subject or ScopeClaim.id, which are also required to be
        // non-blank). This id is server-assigned, so we validate exactly what
        // was asked and no more.
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(principal, "principal");
        Objects.requireNonNull(createdAt, "createdAt");

        // Deliberately lenient: we do not require closedAt to be present when
        // state == CLOSED (or absent when OPEN). The server is the source of
        // truth for that relationship, and validating it here risks rejecting
        // an otherwise-valid response if the server's rules ever change.
        closedAt = (closedAt == null) ? Optional.empty() : closedAt;
    }
}
