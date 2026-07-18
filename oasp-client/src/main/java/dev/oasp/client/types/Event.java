package dev.oasp.client.types;

import java.time.Instant;

/**
 * A normalised session-stream event: the vocabulary every OASP adapter
 * translates provider-native streaming output into. SSE is the first-class
 * transport for this vocabulary; paginated {@code listSessionEvents} is the
 * derive-on-read fallback and doubles as the audit source.
 *
 * <p>{@code Event} is a {@link Resource} (every variant carries {@code
 * resourceType: "Event"} via the {@link #resourceType()} default below),
 * further sub-discriminated by each variant's own wire {@code type} - the
 * same composition {@link AuditEvent#what()} uses one level down.
 *
 * <p>{@code sealed}: {@code permits} is a closed, compiler-checked list of
 * every event kind this SDK version knows how to represent, so a {@code
 * switch} over {@code Event} can be exhaustive. {@link UnknownEvent} is the
 * forward-compatible catch-all for a {@code type} a newer server emits that
 * this SDK version predates.
 *
 * @see AssistantMessageStartEvent
 * @see AssistantMessageTextEvent
 * @see AssistantMessageEndEvent
 * @see AssistantThinkingEvent
 * @see CustomToolUseEvent
 * @see BuiltinToolUseEvent
 * @see StatusEvent
 * @see ErrorEvent
 * @see UnknownEvent
 */
public sealed interface Event extends Resource
        permits AssistantMessageStartEvent,
                AssistantMessageTextEvent,
                AssistantMessageEndEvent,
                AssistantThinkingEvent,
                CustomToolUseEvent,
                BuiltinToolUseEvent,
                StatusEvent,
                ErrorEvent,
                UnknownEvent {

    @Override
    default String resourceType() {
        return "Event";
    }

    /**
     * Opaque, order-comparable identifier for this event within its session
     * stream. Used as the {@code listSessionEvents} pagination cursor and to
     * establish the event ordering conformant adapters must preserve.
     */
    String id();

    /**
     * When the event was emitted, as reported by the server.
     */
    Instant at();
}
