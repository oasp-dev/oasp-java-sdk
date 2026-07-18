package dev.oasp.client.types;

import java.util.Objects;

/**
 * The fallback {@link SessionResource}: used when a server mounts a
 * resource whose {@code type} discriminator this SDK version does not
 * recognise. Mirrors {@link UnknownResource} and {@code UnknownAuditEvent}'s
 * "tolerate an unrecognised discriminator" posture one level down, at the
 * embedded-datatype rather than resource level.
 *
 * @param type    the raw {@code type} discriminator the server sent
 * @param rawJson the mounted resource's raw JSON, preserved verbatim
 */
public record UnknownSessionResource(String type, String rawJson) implements SessionResource {

    public UnknownSessionResource {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(rawJson, "rawJson");
    }
}
