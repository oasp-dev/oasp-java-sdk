package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class AssistantMessageStartEventTest {

    private static final Instant AT = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void constructsWithValidArguments() {
        AssistantMessageStartEvent event = new AssistantMessageStartEvent("evt-1", AT, "msg-1");

        assertThat(event.id()).isEqualTo("evt-1");
        assertThat(event.at()).isEqualTo(AT);
        assertThat(event.messageId()).isEqualTo("msg-1");
        assertThat(event.resourceType()).isEqualTo("Event");
    }

    @Test
    void rejectsNullId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new AssistantMessageStartEvent(null, AT, "msg-1"))
                .withMessageContaining("id");
    }

    @Test
    void rejectsNullAt() {
        assertThatNullPointerException()
                .isThrownBy(() -> new AssistantMessageStartEvent("evt-1", null, "msg-1"))
                .withMessageContaining("at");
    }

    @Test
    void rejectsNullMessageId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new AssistantMessageStartEvent("evt-1", AT, null))
                .withMessageContaining("messageId");
    }
}
