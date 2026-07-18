package dev.oasp.client.types;

import java.util.Objects;
import java.util.Optional;

/**
 * {@code who} performed an interaction: the acting {@link Principal}, and
 * optionally the Principal it acted on behalf of - mirroring the
 * on-behalf-of model's {@code { principal, on_behalf_of? }} shape (e.g. an
 * assistant acting as a member for attribution while remaining
 * scope-pinned).
 *
 * <p>An embedded <em>datatype</em>, private to {@link AuditEvent}.
 *
 * @param principal   the Principal that performed the interaction
 * @param onBehalfOf  the party the principal acted on behalf of, if any
 */
public record AuditWho(PrincipalRef principal, Optional<PrincipalRef> onBehalfOf) {

    public AuditWho {
        Objects.requireNonNull(principal, "principal");
        onBehalfOf = (onBehalfOf == null) ? Optional.empty() : onBehalfOf;
    }
}
