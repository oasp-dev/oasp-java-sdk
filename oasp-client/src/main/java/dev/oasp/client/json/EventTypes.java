package dev.oasp.client.json;

/**
 * The {@code type} discriminator wire values for {@link
 * dev.oasp.client.types.Event} variants, per oasp-standard's normalised
 * session-stream event vocabulary. Collected in one place so {@link
 * EventWriters} and {@link EventReaders} share exactly the same strings.
 */
final class EventTypes {

    static final String ASSISTANT_MESSAGE_START = "assistant_message_start";
    static final String ASSISTANT_MESSAGE_TEXT = "assistant_message_text";
    static final String ASSISTANT_MESSAGE_END = "assistant_message_end";
    static final String ASSISTANT_THINKING = "assistant_thinking";
    static final String CUSTOM_TOOL_USE = "custom_tool_use";
    static final String BUILTIN_TOOL_USE = "builtin_tool_use";
    static final String STATUS = "status";
    static final String ERROR = "error";

    private EventTypes() {}
}
