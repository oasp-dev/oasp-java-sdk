package dev.oasp.client.types;

import java.util.Objects;

/**
 * A lightweight pointer to a {@link Principal} by kind and id, used wherever
 * a resource needs to name an acting party without embedding the full
 * claims-contract record - e.g. {@link Conversation#initiatingPrincipal()}
 * and {@link AuditEvent}'s {@code who}/{@code onBehalfOf}.
 *
 * <p>An embedded <em>datatype</em>, not a {@link Resource}: unlike
 * {@link Principal} itself, it never carries {@code resourceType}.
 *
 * @param kind the kind of principal being referenced
 * @param id   identifier of the referenced {@link Principal} resource
 */
public record PrincipalRef(PrincipalKind kind, String id) {

    public PrincipalRef {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(id, "id");
    }
}
