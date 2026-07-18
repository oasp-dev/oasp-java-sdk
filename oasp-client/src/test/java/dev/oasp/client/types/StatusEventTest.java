package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class StatusEventTest {

    private static final Instant AT = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void constructsWithValidArguments() {
        StatusEvent event = new StatusEvent("evt-1", AT, SessionRunStatus.RUNNING);

        assertThat(event.status()).isEqualTo(SessionRunStatus.RUNNING);
    }

    @Test
    void rejectsNullStatus() {
        assertThatNullPointerException()
                .isThrownBy(() -> new StatusEvent("evt-1", AT, null))
                .withMessageContaining("status");
    }
}
