package dev.oasp.client.error;

/**
 * Thrown when an OASP server rejects a request as unauthenticated ({@code
 * 401}) or unauthorized ({@code 403}).
 *
 * <p>Both statuses are modeled by this one exception type rather than two
 * separate ones because callers overwhelmingly react to them the same way
 * (surface the failure, stop retrying with the same credentials); {@link
 * #status()} is exposed for the caller that does need to tell the two
 * apart, e.g. to prompt for re-authentication only on {@code 401}.
 */
public final class OaspAuthException extends OaspException {

    private final int status;

    public OaspAuthException(int status, String message) {
        super(message);
        this.status = status;
    }

    /**
     * The HTTP status the server responded with: {@code 401} or {@code 403}.
     */
    public int status() {
        return status;
    }
}
