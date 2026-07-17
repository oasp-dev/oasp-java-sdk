package dev.oasp.client.error;

/**
 * Thrown when a request to an OASP server could not be completed at all -
 * a connection failure, a timeout, or anything else that means the server
 * never actually answered.
 *
 * <p>Unlike the other {@link OaspException} subclasses, there is no HTTP
 * status and no {@link dev.oasp.client.types.ErrorEnvelope} to carry: the
 * whole point of this exception is that the server never sent a response
 * for the transport layer to inspect. Instead, the underlying cause (e.g.
 * an {@code IOException} from {@code java.net.http}) is preserved via the
 * standard {@code (message, cause)} constructor.
 *
 * <p>This exception is not produced by {@link ProtocolErrors}: it is
 * created directly by the HTTP transport layer (see issue #7) at the point
 * where a request fails before any response is received.
 */
public final class OaspTransportException extends OaspException {

    public OaspTransportException(String message, Throwable cause) {
        super(message, cause);
    }
}
