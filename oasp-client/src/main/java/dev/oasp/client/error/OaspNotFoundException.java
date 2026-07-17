package dev.oasp.client.error;

/**
 * Thrown when an OASP server responds {@code 404}: the requested resource
 * (e.g. a conversation id) does not exist.
 */
public final class OaspNotFoundException extends OaspException {

    public OaspNotFoundException(String message) {
        super(message);
    }
}
