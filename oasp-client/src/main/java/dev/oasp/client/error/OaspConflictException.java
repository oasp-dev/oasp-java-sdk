package dev.oasp.client.error;

/**
 * Thrown when an OASP server responds {@code 409}: the request conflicts
 * with the resource's current state, e.g. attempting to close a
 * conversation that has already been closed.
 */
public final class OaspConflictException extends OaspException {

    public OaspConflictException(String message) {
        super(message);
    }
}
