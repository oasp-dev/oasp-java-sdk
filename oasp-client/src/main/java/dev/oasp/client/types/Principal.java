package dev.oasp.client.types;

import java.util.List;
import java.util.Objects;

/**
 * A first-class identity: the acting party behind every agent action. OASP
 * models identity as a claims contract - what must be asserted (identity,
 * scope memberships, roles) - rather than prescribing an identity provider.
 *
 * <p>{@code Principal} is a {@link Resource} (carries {@code resourceType:
 * "Principal"}), not an embedded datatype, precisely because it is
 * <em>referenced</em> rather than <em>embedded</em>: every call site that
 * needs to name a principal does so through the lightweight {@link
 * PrincipalRef} datatype instead.
 *
 * @param id                unique identifier of this Principal
 * @param kind              the kind of acting party
 * @param identity          the IdP-agnostic, OIDC-mappable identity assertion
 * @param scopeMemberships  the scopes this principal is a member of; never
 *                          {@code null} and never contains {@code null}
 *                          elements, but may be empty
 * @param roles             IdP-agnostic role names asserted for this
 *                          principal, independent of scope membership; same
 *                          non-null/no-null-elements guarantee as above
 */
public record Principal(
        String id, PrincipalKind kind, PrincipalIdentity identity, List<Scope> scopeMemberships, List<String> roles)
        implements Resource {

    public Principal {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(identity, "identity");
        Objects.requireNonNull(scopeMemberships, "scopeMemberships");
        Objects.requireNonNull(roles, "roles");

        // List.copyOf rejects null elements and returns an unmodifiable
        // snapshot, so this Principal can't be mutated later by a caller
        // holding onto (and changing) the list they passed in.
        scopeMemberships = List.copyOf(scopeMemberships);
        roles = List.copyOf(roles);
    }

    @Override
    public String resourceType() {
        return "Principal";
    }
}
