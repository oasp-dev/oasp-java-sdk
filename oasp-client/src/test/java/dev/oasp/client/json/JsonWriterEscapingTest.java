package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** String escaping rules: quotes, backslashes, control characters, and forward slash. */
class JsonWriterEscapingTest {

    @Test
    void writesPlainString() {
        assertThat(JsonWriter.write("hello")).isEqualTo("\"hello\"");
    }

    @Test
    void escapesQuotesAndBackslashes() {
        assertThat(JsonWriter.write("say \"hi\"")).isEqualTo("\"say \\\"hi\\\"\"");
        assertThat(JsonWriter.write("a\\b")).isEqualTo("\"a\\\\b\"");
    }

    @Test
    void escapesNamedControlCharacters() {
        assertThat(JsonWriter.write("line1\nline2")).isEqualTo("\"line1\\nline2\"");
        assertThat(JsonWriter.write("a\tb")).isEqualTo("\"a\\tb\"");
        assertThat(JsonWriter.write("a\rb")).isEqualTo("\"a\\rb\"");
        assertThat(JsonWriter.write("a\bb")).isEqualTo("\"a\\bb\"");
        assertThat(JsonWriter.write("a\fb")).isEqualTo("\"a\\fb\"");
    }

    @Test
    void escapesOtherControlCharactersAsUnicode() {
        // U+0001 (start of heading) has no named JSON escape, so it must
        // fall back to a generic unicode escape sequence.
        String startOfHeading = String.valueOf((char) 1);
        assertThat(JsonWriter.write("a" + startOfHeading + "b")).isEqualTo("\"a\\u0001b\"");
    }

    @Test
    void leavesForwardSlashUnescaped() {
        assertThat(JsonWriter.write("a/b")).isEqualTo("\"a/b\"");
    }
}
