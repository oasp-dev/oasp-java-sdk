package dev.oasp.client.json;

/**
 * Thrown when JSON text is malformed, or when a JSON tree does not have the
 * shape a mapping to/from a protocol type expects (e.g. a JSON object where
 * an array was required).
 *
 * <p>Deliberately {@code extends RuntimeException} directly, NOT {@link
 * dev.oasp.client.error.OaspException}: that hierarchy is {@code sealed}
 * with a closed, compiler-checked list of permitted subclasses (see {@link
 * dev.oasp.client.error.OaspException}), and this failure doesn't belong to
 * it. A malformed JSON payload is a serialisation-layer problem, not a
 * failed OASP request - it can happen before an HTTP call is even made (e.g.
 * serialising a request body) or after one succeeds at the transport level
 * but returns a body this SDK can't parse. Deciding whether/how a {@code
 * JsonException} should surface as an {@code OaspException} (for example,
 * wrapped as an {@code OaspProtocolException} when it results from an
 * unparsable server response) is left to the HTTP transport layer (issue
 * #7), which has the request/response context needed to make that call.
 */
public class JsonException extends RuntimeException {

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
