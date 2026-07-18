package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class AssistantMessageEndEventTest {

    private static final Instant AT = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void constructsWithValidArguments() {
        AssistantMessageEndEvent event = new AssistantMessageEndEvent("evt-1", AT, "msg-1");

        assertThat(event.id()).isEqualTo("evt-1");
        assertThat(event.messageId()).isEqualTo("msg-1");
    }

    @Test
    void rejectsNullMessageId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new AssistantMessageEndEvent("evt-1", AT, null))
                .withMessageContaining("messageId");
    }
}
