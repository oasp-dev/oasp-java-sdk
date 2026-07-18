package dev.oasp.client.types;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * An invocation of a provider-hosted builtin tool (as opposed to a custom
 * tool defined on the AgentDefinition - see {@link CustomToolUseEvent}).
 *
 * @param id         opaque, order-comparable event id (see {@link Event#id()})
 * @param at         when the event was emitted
 * @param toolUseId  identifier correlating this tool use to its eventual result
 * @param name       name of the builtin tool being invoked
 * @param input      the input arguments passed to the tool; never {@code null}
 */
public record BuiltinToolUseEvent(String id, Instant at, String toolUseId, String name, Map<String, Object> input)
        implements Event {

    public BuiltinToolUseEvent {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(at, "at");
        Objects.requireNonNull(toolUseId, "toolUseId");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(input, "input");

        input = Map.copyOf(input);
    }
}
