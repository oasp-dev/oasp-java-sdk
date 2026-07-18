package dev.oasp.client.json;

import dev.oasp.client.types.AssistantMessageEndEvent;
import dev.oasp.client.types.AssistantMessageStartEvent;
import dev.oasp.client.types.AssistantMessageTextEvent;
import dev.oasp.client.types.AssistantThinkingEvent;
import dev.oasp.client.types.BuiltinToolUseEvent;
import dev.oasp.client.types.CustomToolUseEvent;
import dev.oasp.client.types.ErrorEvent;
import dev.oasp.client.types.Event;
import dev.oasp.client.types.SessionRunStatus;
import dev.oasp.client.types.StatusEvent;
import dev.oasp.client.types.UnknownEvent;
import java.util.Map;

/**
 * Dispatches a parsed {@link Event} on its {@code type} discriminator; an
 * unrecognised value maps to {@link UnknownEvent} instead of throwing, the
 * forward-compatible fallback the {@code Event} sealed hierarchy exists for.
 * {@code originalJson} is the exact text passed to {@code read()}, threaded
 * through only to populate an {@link UnknownEvent}'s {@code rawJson}
 * byte-for-byte.
 */
final class EventReaders {

    private EventReaders() {}

    static Event mapEvent(Map<String, Object> obj, String originalJson) {
        var id = JsonFields.string(obj, "id");
        var at = JsonFields.instant(obj, "at");
        var type = JsonFields.string(obj, "type");

        return switch (type) {
            case EventTypes.ASSISTANT_MESSAGE_START ->
                    new AssistantMessageStartEvent(id, at, JsonFields.string(obj, "messageId"));
            case EventTypes.ASSISTANT_MESSAGE_END ->
                    new AssistantMessageEndEvent(id, at, JsonFields.string(obj, "messageId"));
            case EventTypes.ASSISTANT_MESSAGE_TEXT -> new AssistantMessageTextEvent(
                    id, at, JsonFields.string(obj, "messageId"), JsonFields.string(obj, "delta"));
            case EventTypes.ASSISTANT_THINKING -> new AssistantThinkingEvent(id, at, JsonFields.string(obj, "delta"));
            case EventTypes.CUSTOM_TOOL_USE -> new CustomToolUseEvent(
                    id,
                    at,
                    JsonFields.string(obj, "toolUseId"),
                    JsonFields.string(obj, "name"),
                    JsonFields.object(obj, "input"));
            case EventTypes.BUILTIN_TOOL_USE -> new BuiltinToolUseEvent(
                    id,
                    at,
                    JsonFields.string(obj, "toolUseId"),
                    JsonFields.string(obj, "name"),
                    JsonFields.object(obj, "input"));
            case EventTypes.STATUS ->
                    new StatusEvent(id, at, JsonFields.enumValue(obj, "status", SessionRunStatus::fromWire));
            case EventTypes.ERROR ->
                    new ErrorEvent(id, at, JsonFields.string(obj, "message"), JsonFields.bool(obj, "recoverable"));
            default -> new UnknownEvent(id, at, type, originalJson);
        };
    }
}
