package dev.oasp.client.json;

import dev.oasp.client.types.AssistantMessageEndEvent;
import dev.oasp.client.types.AssistantMessageStartEvent;
import dev.oasp.client.types.AssistantMessageTextEvent;
import dev.oasp.client.types.AssistantThinkingEvent;
import dev.oasp.client.types.BuiltinToolUseEvent;
import dev.oasp.client.types.CustomToolUseEvent;
import dev.oasp.client.types.ErrorEvent;
import dev.oasp.client.types.Event;
import dev.oasp.client.types.StatusEvent;
import dev.oasp.client.types.UnknownEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Writes one {@link Event}, dispatched on its concrete variant. An {@link
 * UnknownEvent} is a special case handled entirely in {@link
 * HandRolledJsonCodec#write(Object)} (emit its {@code rawJson} verbatim); it
 * only reaches the fallback branch here if it turns up nested inside some
 * other value being written, which nothing in this SDK currently does.
 */
final class EventWriters {

    private EventWriters() {}

    static Map<String, Object> write(Event event) {
        return switch (event) {
            case AssistantMessageStartEvent e -> withMessageId(e, EventTypes.ASSISTANT_MESSAGE_START, e.messageId());
            case AssistantMessageEndEvent e -> withMessageId(e, EventTypes.ASSISTANT_MESSAGE_END, e.messageId());
            case AssistantMessageTextEvent e -> {
                Map<String, Object> tree = base(e, EventTypes.ASSISTANT_MESSAGE_TEXT);
                tree.put("messageId", e.messageId());
                tree.put("delta", e.delta());
                yield tree;
            }
            case AssistantThinkingEvent e -> {
                Map<String, Object> tree = base(e, EventTypes.ASSISTANT_THINKING);
                tree.put("delta", e.delta());
                yield tree;
            }
            case CustomToolUseEvent e ->
                    withToolUse(e, EventTypes.CUSTOM_TOOL_USE, e.toolUseId(), e.name(), e.input());
            case BuiltinToolUseEvent e ->
                    withToolUse(e, EventTypes.BUILTIN_TOOL_USE, e.toolUseId(), e.name(), e.input());
            case StatusEvent e -> {
                Map<String, Object> tree = base(e, EventTypes.STATUS);
                tree.put("status", e.status().wireValue());
                yield tree;
            }
            case ErrorEvent e -> {
                Map<String, Object> tree = base(e, EventTypes.ERROR);
                tree.put("message", e.message());
                tree.put("recoverable", e.recoverable());
                yield tree;
            }
            case UnknownEvent unknown -> JsonTrees.asObject(JsonParser.parse(unknown.rawJson()), "root");
        };
    }

    private static Map<String, Object> base(Event event, String type) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("resourceType", event.resourceType());
        tree.put("id", event.id());
        tree.put("at", event.at().toString());
        tree.put("type", type);
        return tree;
    }

    private static Map<String, Object> withMessageId(Event event, String type, String messageId) {
        Map<String, Object> tree = base(event, type);
        tree.put("messageId", messageId);
        return tree;
    }

    private static Map<String, Object> withToolUse(
            Event event, String type, String toolUseId, String name, Map<String, Object> input) {
        Map<String, Object> tree = base(event, type);
        tree.put("toolUseId", toolUseId);
        tree.put("name", name);
        tree.put("input", input);
        return tree;
    }
}
