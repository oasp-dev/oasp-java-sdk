package dev.oasp.client.types;

import java.util.Objects;

/**
 * The body of a protocol error response from an OASP server: a short machine
 * -readable {@code code} plus a human-readable {@code message} explaining
 * what went wrong.
 *
 * <p>This is inbound server data (see {@code dev.oasp.client.error}, which
 * maps HTTP status codes and this envelope to the SDK's exception
 * hierarchy), so - matching the convention used across this package - it is
 * validated leniently: {@code code} and {@code message} must be non-null,
 * but no blank check is applied. A server-provided error code or message we
 * consider "blank" might still be meaningful to a human reading logs, and
 * rejecting it here would only turn a decodable error response into an SDK
 * failure.
 *
 * @param code    a short, machine-readable identifier for the error, e.g.
 *                {@code "conversation_already_closed"}
 * @param message a human-readable description of what went wrong
 */
public record ErrorEnvelope(String code, String message) {

    public ErrorEnvelope {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(message, "message");
    }
}
