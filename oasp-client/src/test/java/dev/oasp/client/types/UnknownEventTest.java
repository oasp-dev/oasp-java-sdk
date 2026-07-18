package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class UnknownEventTest {

    private static final Instant AT = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void constructsWithValidArguments() {
        UnknownEvent event = new UnknownEvent("evt-1", AT, "screen_share", "{\"type\":\"screen_share\"}");

        assertThat(event.type()).isEqualTo("screen_share");
        assertThat(event.rawJson()).isEqualTo("{\"type\":\"screen_share\"}");
        assertThat(event.resourceType()).isEqualTo("Event");
    }

    @Test
    void rejectsNullType() {
        assertThatNullPointerException()
                .isThrownBy(() -> new UnknownEvent("evt-1", AT, null, "{}"))
                .withMessageContaining("type");
    }

    @Test
    void rejectsNullRawJson() {
        assertThatNullPointerException()
                .isThrownBy(() -> new UnknownEvent("evt-1", AT, "screen_share", null))
                .withMessageContaining("rawJson");
    }
}
