package dev.oasp.client.json;

/**
 * The {@code type} discriminator strings that identify each concrete {@link
 * dev.oasp.client.types.AuditEvent} in JSON. Collected in one place so
 * they're trivial to change in a single spot once issue #2 verifies them
 * against the actual OASP protocol specification.
 *
 * <p>These values are ASSUMED for v0 - {@code "conversation.created"} and
 * {@code "conversation.closed"} are guesses at what a real OASP server
 * sends, not confirmed spec values.
 */
final class AuditEventTypes {

    static final String CONVERSATION_CREATED = "conversation.created";
    static final String CONVERSATION_CLOSED = "conversation.closed";

    private AuditEventTypes() {}
}
