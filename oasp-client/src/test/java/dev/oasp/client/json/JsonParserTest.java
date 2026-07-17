package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JsonParserTest {

    @Test
    void parsesNull() {
        assertThat(JsonParser.parse("null")).isNull();
    }

    @Test
    void parsesBooleans() {
        assertThat(JsonParser.parse("true")).isEqualTo(true);
        assertThat(JsonParser.parse("false")).isEqualTo(false);
    }

    @Test
    void parsesIntegerAsLong() {
        assertThat(JsonParser.parse("42")).isEqualTo(42L).isInstanceOf(Long.class);
        assertThat(JsonParser.parse("-7")).isEqualTo(-7L);
    }

    @Test
    void parsesDecimalAndExponentAsDouble() {
        assertThat(JsonParser.parse("3.14")).isEqualTo(3.14).isInstanceOf(Double.class);
        assertThat(JsonParser.parse("1e3")).isEqualTo(1000.0);
        assertThat(JsonParser.parse("2.5E-2")).isEqualTo(0.025);
    }

    @Test
    void parsesPlainString() {
        assertThat(JsonParser.parse("\"hello\"")).isEqualTo("hello");
    }

    @Test
    void parsesEscapedString() {
        assertThat(JsonParser.parse("\"say \\\"hi\\\"\"")).isEqualTo("say \"hi\"");
        assertThat(JsonParser.parse("\"a\\\\b\"")).isEqualTo("a\\b");
        assertThat(JsonParser.parse("\"line1\\nline2\"")).isEqualTo("line1\nline2");
        assertThat(JsonParser.parse("\"\\u0041\"")).isEqualTo("A");
    }

    @Test
    void parsesEmptyObjectAndArray() {
        assertThat(JsonParser.parse("{}")).isEqualTo(Map.of());
        assertThat(JsonParser.parse("[]")).isEqualTo(List.of());
    }

    @Test
    void parsesNestedObjectPreservingKeyOrder() {
        Object result = JsonParser.parse("{\"b\":1,\"a\":2,\"c\":[1,2,3]}");

        assertThat(result).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) result;
        // Order matters: it should reflect the source text, not be
        // re-sorted, which is why the mapping layer uses LinkedHashMap.
        assertThat(map.keySet()).containsExactly("b", "a", "c");
        assertThat(map.get("c")).isEqualTo(List.of(1L, 2L, 3L));
    }

    @Test
    void ignoresLeadingAndTrailingWhitespace() {
        assertThat(JsonParser.parse("   \n\t 42  \n")).isEqualTo(42L);
    }

    @Test
    void throwsOnTruncatedObject() {
        assertThatThrownBy(() -> JsonParser.parse("{\"a\":1")).isInstanceOf(JsonException.class);
    }

    @Test
    void throwsOnTrailingGarbage() {
        assertThatThrownBy(() -> JsonParser.parse("{\"a\":1} garbage"))
                .isInstanceOf(JsonException.class);
    }

    @Test
    void throwsOnUnterminatedString() {
        assertThatThrownBy(() -> JsonParser.parse("\"unterminated"))
                .isInstanceOf(JsonException.class);
    }

    @Test
    void throwsOnNonJsonInput() {
        assertThatThrownBy(() -> JsonParser.parse("not json at all"))
                .isInstanceOf(JsonException.class);
    }

    @Test
    void throwsOnEmptyInput() {
        assertThatThrownBy(() -> JsonParser.parse("")).isInstanceOf(JsonException.class);
        assertThatThrownBy(() -> JsonParser.parse("   ")).isInstanceOf(JsonException.class);
    }

    @Test
    void throwsOnTrailingCommaInArray() {
        assertThatThrownBy(() -> JsonParser.parse("[1,2,]")).isInstanceOf(JsonException.class);
    }

    @Test
    void errorMessageIncludesPosition() {
        assertThatThrownBy(() -> JsonParser.parse("{\"a\":1} garbage"))
                .isInstanceOf(JsonException.class)
                .hasMessageContaining("position");
    }
}
