package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;

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
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Round-trips every {@link Event} variant, plus the unrecognised-{@code type} fallback. */
class EventRoundTripTest {

    private static final Instant AT = Instant.parse("2026-01-01T00:00:00Z");

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

    @Test
    void roundTripsAssistantMessageStartEvent() {
        assertRoundTrips(new AssistantMessageStartEvent("evt-1", AT, "msg-1"));
    }

    @Test
    void roundTripsAssistantMessageTextEvent() {
        assertRoundTrips(new AssistantMessageTextEvent("evt-1", AT, "msg-1", "Hello"));
    }

    @Test
    void roundTripsAssistantMessageEndEvent() {
        assertRoundTrips(new AssistantMessageEndEvent("evt-1", AT, "msg-1"));
    }

    @Test
    void roundTripsAssistantThinkingEvent() {
        assertRoundTrips(new AssistantThinkingEvent("evt-1", AT, "Thinking..."));
    }

    @Test
    void roundTripsCustomToolUseEvent() {
        assertRoundTrips(new CustomToolUseEvent("evt-1", AT, "tool-use-1", "search", Map.of("query", "oasp")));
    }

    @Test
    void roundTripsBuiltinToolUseEvent() {
        assertRoundTrips(new BuiltinToolUseEvent("evt-1", AT, "tool-use-1", "bash", Map.of("command", "ls")));
    }

    @Test
    void roundTripsStatusEvent() {
        assertRoundTrips(new StatusEvent("evt-1", AT, SessionRunStatus.RUNNING));
    }

    @Test
    void roundTripsErrorEvent() {
        assertRoundTrips(new ErrorEvent("evt-1", AT, "provider timed out", true));
    }

    @Test
    void unrecognisedEventTypeMapsToUnknownEventNotAnException() {
        String json = "{\"resourceType\":\"Event\",\"id\":\"evt-1\",\"at\":\"2026-01-01T00:00:00Z\","
                + "\"type\":\"screen_share\",\"streamId\":\"stream-1\"}";

        Event event = codec.read(json, Event.class);

        assertThat(event).isInstanceOf(UnknownEvent.class);
        UnknownEvent unknown = (UnknownEvent) event;
        assertThat(unknown.type()).isEqualTo("screen_share");
        assertThat(unknown.rawJson()).isEqualTo(json);
        assertThat(unknown.id()).isEqualTo("evt-1");
        assertThat(unknown.at()).isEqualTo(AT);
    }

    @Test
    void writingAnUnknownEventEmitsRawJsonVerbatim() {
        String rawJson = "{\"resourceType\":\"Event\",\"id\":\"evt-1\",\"at\":\"2026-01-01T00:00:00Z\","
                + "\"type\":\"screen_share\",\"extra\":\"field\"}";
        UnknownEvent unknown = new UnknownEvent("evt-1", AT, "screen_share", rawJson);

        // Byte-for-byte, not just structurally equal - even the unmapped
        // "extra" field must survive.
        assertThat(codec.write(unknown)).isEqualTo(rawJson);
    }

    private void assertRoundTrips(Event event) {
        Event result = codec.read(codec.write(event), Event.class);
        assertThat(result).isEqualTo(event);
    }
}
