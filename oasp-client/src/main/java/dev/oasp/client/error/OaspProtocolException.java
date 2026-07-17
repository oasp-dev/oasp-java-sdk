package dev.oasp.client.error;

import dev.oasp.client.types.ErrorEnvelope;
import java.util.Optional;

/**
 * Thrown when an OASP server responds with a protocol-level error status
 * that this SDK version has no dedicated exception for (i.e. anything other
 * than the {@code 401}/{@code 403}/{@code 404}/{@code 409} cases already
 * covered by {@link OaspAuthException}, {@link OaspNotFoundException}, and
 * {@link OaspConflictException}).
 *
 * <p>This is the SDK's forward-compatibility escape hatch: a newer server
 * version can start returning an error status or code this SDK doesn't yet
 * model, and instead of that turning into an opaque failure, the raw {@code
 * status} and (when present) {@link ErrorEnvelope} are preserved verbatim so
 * the caller can still inspect what the server actually said.
 *
 * <p>Unlike the other mapped exceptions, this one keeps the structured
 * {@link ErrorEnvelope} itself (not just its {@code message}) because
 * callers handling an error type the SDK doesn't specifically model are the
 * ones most likely to need the raw {@code code} too, e.g. to branch on it
 * themselves or log it for later triage.
 */
public final class OaspProtocolException extends OaspException {

    private final int status;
    private final ErrorEnvelope envelope;

    public OaspProtocolException(int status, ErrorEnvelope envelope, String message) {
        super(message);
        this.status = status;
        this.envelope = envelope;
    }

    /**
     * The HTTP status the server responded with.
     */
    public int status() {
        return status;
    }

    /**
     * The server's raw error envelope, if one was sent and successfully
     * parsed. Empty when the server sent no body, or a body this SDK could
     * not parse as an {@link ErrorEnvelope}.
     */
    public Optional<ErrorEnvelope> envelope() {
        return Optional.ofNullable(envelope);
    }
}
