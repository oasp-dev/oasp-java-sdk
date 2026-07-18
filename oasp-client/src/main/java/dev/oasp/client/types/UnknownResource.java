package dev.oasp.client.types;

import java.util.Objects;

/**
 * The fallback {@link Resource}: used when a server reports a
 * {@code resourceType} this SDK version does not recognise.
 *
 * <p>This is the general form of the {@code UnknownAuditEvent} fallback this
 * SDK originally hand-built just for {@code AuditEvent.what} - the pattern
 * that motivated oasp-standard's own {@code resourceType} proposal (see
 * {@code docs/proposals/0001-resource-type-discriminator.md}), which
 * normatively requires every conformant reader to tolerate an unrecognised
 * {@code resourceType} rather than hard-failing. Preserving {@link #rawJson()}
 * verbatim means a newer server can introduce resource types without older
 * SDK versions losing information or failing to deserialize the response.
 *
 * <p>{@code rawJson} is a plain {@code String}, not a parsed tree, because
 * this module has no JSON library (see the zero-runtime-dependency rule on
 * {@code oasp-client}); callers who need structure out of it parse it
 * themselves.
 *
 * <p>Note: this type only models the fallback shape. Actually recognising an
 * unknown {@code resourceType} and routing it here is done by the JSON
 * deserialization layer, not by this record.
 *
 * @param resourceType the raw {@code resourceType} discriminator the server
 *                     sent, e.g. {@code "Deployment"}
 * @param rawJson      the resource's raw JSON, preserved verbatim
 */
public record UnknownResource(String resourceType, String rawJson) implements Resource {

    public UnknownResource {
        // resourceType is what makes this resource "unknown" in the first
        // place - unlike most inbound identifiers elsewhere in this package,
        // it must carry information about what was actually received.
        Objects.requireNonNull(resourceType, "resourceType");
        Objects.requireNonNull(rawJson, "rawJson");
    }
}
