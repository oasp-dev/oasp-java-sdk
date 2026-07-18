package dev.oasp.client.types;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * An invocation of a custom tool defined on the AgentDefinition (as opposed
 * to a provider-hosted builtin - see {@link BuiltinToolUseEvent}).
 *
 * @param id         opaque, order-comparable event id (see {@link Event#id()})
 * @param at         when the event was emitted
 * @param toolUseId  identifier correlating this tool use to its eventual result
 * @param name       name of the custom tool being invoked
 * @param input      the input arguments passed to the tool; never {@code null}
 */
public record CustomToolUseEvent(String id, Instant at, String toolUseId, String name, Map<String, Object> input)
        implements Event {

    public CustomToolUseEvent {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(at, "at");
        Objects.requireNonNull(toolUseId, "toolUseId");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(input, "input");

        input = Map.copyOf(input);
    }
}
