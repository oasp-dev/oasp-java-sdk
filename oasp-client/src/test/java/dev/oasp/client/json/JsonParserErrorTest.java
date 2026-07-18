package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/** Malformed input: every way {@link JsonParser} is expected to throw {@link JsonException}. */
class JsonParserErrorTest {

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
