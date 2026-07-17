package dev.oasp.client.error;

import dev.oasp.client.types.ErrorEnvelope;

/**
 * Maps a non-success OASP protocol response - an HTTP status plus, when the
 * server sent one, an {@link ErrorEnvelope} - to the appropriate {@link
 * OaspException} subclass.
 *
 * <p>This class only handles the case where the server actually answered
 * with an error status. It deliberately never produces {@link
 * OaspTransportException}: that exception represents the absence of a
 * server response at all (connection failure, timeout, etc.), which is
 * created directly by the HTTP transport layer (issue #7), not by this
 * mapper.
 */
public final class ProtocolErrors {

    // Not meant to be instantiated - this is a pure static function holder.
    private ProtocolErrors() {}

    /**
     * Builds the {@link OaspException} that corresponds to the given
     * protocol error response.
     *
     * @param status   the HTTP status the server responded with
     * @param envelope the server's parsed error body, or {@code null} if the
     *                 server sent no body, or a body this SDK could not
     *                 parse as an {@link ErrorEnvelope}
     * @return the mapped exception, ready to be thrown by the caller
     */
    public static OaspException fromResponse(int status, ErrorEnvelope envelope) {
        // Every mapped exception (other than OaspProtocolException, which
        // keeps the whole envelope) uses the envelope's message verbatim
        // when present, so getMessage() reflects what the server actually
        // said. When there's no envelope to draw from, fall back to a
        // status-only message rather than throwing with a null/blank one.
        String message = (envelope != null) ? envelope.message() : defaultMessage(status);

        return switch (status) {
            case 401, 403 -> new OaspAuthException(status, message);
            case 404 -> new OaspNotFoundException(message);
            case 409 -> new OaspConflictException(message);
            default -> new OaspProtocolException(status, envelope, message);
        };
    }

    private static String defaultMessage(int status) {
        return "OASP request failed with status " + status;
    }
}
