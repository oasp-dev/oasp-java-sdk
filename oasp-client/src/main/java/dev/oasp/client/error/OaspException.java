package dev.oasp.client.error;

/**
 * Base type for every exception this SDK throws in response to a failed
 * OASP request.
 *
 * <p>Deliberately {@code extends RuntimeException} (unchecked): callers of
 * an HTTP-backed SDK already have to handle failure at essentially every
 * call site, so forcing a checked-exception signature (and the
 * {@code throws} boilerplate that comes with it) buys little and costs a lot
 * of ceremony. Callers who want to handle specific failures can still catch
 * the concrete subclasses below.
 *
 * <p>This class is {@code sealed}: {@code permits} is a closed,
 * compiler-checked list of every kind of failure this SDK version knows how
 * to represent, mirroring the same technique used for {@link
 * dev.oasp.client.types.AuditEvent}. A {@code switch} over {@code
 * OaspException} can therefore be exhaustive with no {@code default}
 * branch. Each permitted subclass is declared {@code final} because these
 * are leaf types - Java requires every direct subclass of a sealed type to
 * itself be {@code final}, {@code sealed}, or {@code non-sealed}.
 *
 * @see OaspTransportException
 * @see OaspAuthException
 * @see OaspNotFoundException
 * @see OaspConflictException
 * @see OaspProtocolException
 */
public sealed class OaspException extends RuntimeException
        permits OaspTransportException,
                OaspAuthException,
                OaspNotFoundException,
                OaspConflictException,
                OaspProtocolException {

    public OaspException(String message) {
        super(message);
    }

    public OaspException(String message, Throwable cause) {
        super(message, cause);
    }
}
