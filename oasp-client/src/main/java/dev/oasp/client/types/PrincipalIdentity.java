package dev.oasp.client.types;

import java.util.Objects;
import java.util.Optional;

/**
 * The claims-contract identity assertion a {@link Principal} carries: what
 * an implementation asserts about the acting party, shaped to be
 * OIDC-mappable at the claims boundary ({@code subject}/{@code issuer} map
 * to {@code sub}/{@code iss}) without requiring OIDC.
 *
 * <p>An embedded <em>datatype</em>, private to {@link Principal} - it never
 * appears anywhere else and never carries {@code resourceType}.
 *
 * @param subject     stable subject identifier for this principal
 * @param issuer      identifier of the asserting identity provider, if any
 * @param displayName human-readable display name, if asserted
 * @param email       email address, if asserted
 */
public record PrincipalIdentity(
        String subject, Optional<String> issuer, Optional<String> displayName, Optional<String> email) {

    public PrincipalIdentity {
        Objects.requireNonNull(subject, "subject");

        // Optional fields normalized from a plain null (e.g. a deserializer
        // that doesn't know about Optional) to Optional.empty(), same
        // convention as Conversation.closedAt in the prior protocol types.
        issuer = (issuer == null) ? Optional.empty() : issuer;
        displayName = (displayName == null) ? Optional.empty() : displayName;
        email = (email == null) ? Optional.empty() : email;
    }
}
