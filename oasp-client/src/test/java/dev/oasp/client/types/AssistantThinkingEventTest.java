package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class AssistantThinkingEventTest {

    private static final Instant AT = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void constructsWithValidArguments() {
        AssistantThinkingEvent event = new AssistantThinkingEvent("evt-1", AT, "pondering...");

        assertThat(event.id()).isEqualTo("evt-1");
        assertThat(event.delta()).isEqualTo("pondering...");
    }

    @Test
    void rejectsNullDelta() {
        assertThatNullPointerException()
                .isThrownBy(() -> new AssistantThinkingEvent("evt-1", AT, null))
                .withMessageContaining("delta");
    }
}
