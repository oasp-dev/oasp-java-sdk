package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CustomToolUseEventTest {

    private static final Instant AT = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void constructsWithValidArguments() {
        CustomToolUseEvent event =
                new CustomToolUseEvent("evt-1", AT, "tool-use-1", "search", Map.of("query", "cats"));

        assertThat(event.toolUseId()).isEqualTo("tool-use-1");
        assertThat(event.name()).isEqualTo("search");
        assertThat(event.input()).containsEntry("query", "cats");
    }

    @Test
    void defensivelyCopiesInput() {
        Map<String, Object> input = new HashMap<>();
        input.put("query", "cats");

        CustomToolUseEvent event = new CustomToolUseEvent("evt-1", AT, "tool-use-1", "search", input);
        input.put("limit", 10);

        assertThat(event.input()).hasSize(1);
    }

    @Test
    void rejectsNullInput() {
        assertThatNullPointerException()
                .isThrownBy(() -> new CustomToolUseEvent("evt-1", AT, "tool-use-1", "search", null))
                .withMessageContaining("input");
    }

    @Test
    void rejectsNullName() {
        assertThatNullPointerException()
                .isThrownBy(() -> new CustomToolUseEvent("evt-1", AT, "tool-use-1", null, Map.of()))
                .withMessageContaining("name");
    }

    @Test
    void rejectsNullToolUseId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new CustomToolUseEvent("evt-1", AT, null, "search", Map.of()))
                .withMessageContaining("toolUseId");
    }
}
