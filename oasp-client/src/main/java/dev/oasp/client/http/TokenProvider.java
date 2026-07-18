package dev.oasp.client.http;

/**
 * Supplies the bearer token for the {@code Authorization} header of every
 * OASP request. Called once per request (not cached by the transport), so
 * an implementation backed by a rotating/short-lived token can hand out a
 * fresh one each time without the SDK holding a stale value.
 */
@FunctionalInterface
public interface TokenProvider {

    /** Returns the bearer token to send as {@code Authorization: Bearer <token>}. */
    String getToken();
}
