package dev.oasp.client.types;

import java.util.List;
import java.util.Objects;

/**
 * The identity a {@link Conversation} is opened on behalf of: a {@code subject}
 * (who this is) plus the {@link ScopeClaim}s that describe what they have
 * access to.
 *
 * @param subject the identifier of the calling identity, e.g. a user id
 * @param claims  the scope claims held by this principal; never {@code null}
 *                and never contains {@code null} elements, but may be empty
 */
public record Principal(String subject, List<ScopeClaim> claims) {

    public Principal {
        Objects.requireNonNull(subject, "subject");
        if (subject.isBlank()) {
            throw new IllegalArgumentException("subject must not be blank");
        }
        Objects.requireNonNull(claims, "claims");

        // List.copyOf does three jobs at once: it rejects a null list (already
        // handled above, but also guards against a future refactor removing that
        // check), it rejects any null element inside the list, and it returns an
        // unmodifiable snapshot. That snapshot is what gets stored, so callers
        // can't mutate this Principal after construction by holding onto (and
        // changing) the list they passed in.
        claims = List.copyOf(claims);
    }
}
