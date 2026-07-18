package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;

class BuiltinToolUseEventTest {

    private static final Instant AT = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void constructsWithValidArguments() {
        BuiltinToolUseEvent event =
                new BuiltinToolUseEvent("evt-1", AT, "tool-use-1", "web_search", Map.of("query", "cats"));

        assertThat(event.toolUseId()).isEqualTo("tool-use-1");
        assertThat(event.name()).isEqualTo("web_search");
        assertThat(event.input()).containsEntry("query", "cats");
    }

    @Test
    void rejectsNullInput() {
        assertThatNullPointerException()
                .isThrownBy(() -> new BuiltinToolUseEvent("evt-1", AT, "tool-use-1", "web_search", null))
                .withMessageContaining("input");
    }

    @Test
    void rejectsNullName() {
        assertThatNullPointerException()
                .isThrownBy(() -> new BuiltinToolUseEvent("evt-1", AT, "tool-use-1", null, Map.of()))
                .withMessageContaining("name");
    }
}
