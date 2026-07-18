package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class ErrorEventTest {

    private static final Instant AT = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void constructsWithValidArguments() {
        ErrorEvent event = new ErrorEvent("evt-1", AT, "tool timed out", true);

        assertThat(event.message()).isEqualTo("tool timed out");
        assertThat(event.recoverable()).isTrue();
    }

    @Test
    void rejectsNullMessage() {
        assertThatNullPointerException()
                .isThrownBy(() -> new ErrorEvent("evt-1", AT, null, false))
                .withMessageContaining("message");
    }
}
