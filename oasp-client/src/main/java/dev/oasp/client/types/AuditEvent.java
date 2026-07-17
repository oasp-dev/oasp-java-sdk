package dev.oasp.client.types;

import java.time.Instant;

/**
 * An audit event reported by an OASP server for a {@link Conversation}: a
 * record of something that happened, who caused it, and when.
 *
 * <p>This interface is {@code sealed}: {@code permits} below is a closed,
 * compiler-checked list of every type allowed to implement it. That buys two
 * things a plain (non-sealed) interface can't offer:
 *
 * <ul>
 *   <li>a {@code switch} over {@code AuditEvent} can cover all permitted
 *       types with no {@code default} branch, and the compiler rejects the
 *       switch if a case is missing - "did I handle every kind of event?"
 *       becomes a compile error instead of a runtime bug;
 *   <li>the set of possible event kinds is conformance-checkable: anyone
 *       reading this file sees the complete taxonomy in one place, rather
 *       than having to search the codebase for implementers.
 * </ul>
 *
 * <p>The obvious risk with a closed set is forward-compatibility: what
 * happens when a newer OASP server starts emitting an event type this SDK
 * version has never heard of? That's what {@link UnknownAuditEvent} is for -
 * it's a permitted member of this sealed hierarchy that acts as a catch-all,
 * preserving the common envelope fields plus the raw, unrecognised payload
 * instead of the SDK failing to deserialize the event at all. (Actually
 * routing an unrecognised event type to {@code UnknownAuditEvent} is the
 * job of the JSON deserialization layer, not this type.)
 *
 * @see ConversationCreated
 * @see ConversationClosed
 * @see UnknownAuditEvent
 */
public sealed interface AuditEvent permits ConversationCreated, ConversationClosed, UnknownAuditEvent {

    /**
     * The id of the {@link Conversation} this event occurred on.
     */
    String conversationId();

    /**
     * When the event occurred, as reported by the server.
     */
    Instant occurredAt();

    /**
     * The principal that caused this event.
     */
    Principal actor();
}
