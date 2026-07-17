package dev.oasp.client.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A small recursive-descent parser that turns JSON text into JSON's own
 * generic value model: object → {@code Map<String,Object>} (a {@link
 * LinkedHashMap}, so key order in the source text is preserved), array →
 * {@code List<Object>}, string → {@code String}, integer → {@code Long},
 * decimal/exponent number → {@code Double}, {@code true}/{@code false} →
 * {@code Boolean}, {@code null} → Java {@code null}.
 *
 * <p>This class has no knowledge of {@code dev.oasp.client.types}; mapping
 * the tree it produces to a protocol type (e.g. {@link
 * dev.oasp.client.types.Conversation}) is {@link HandRolledJsonCodec}'s job.
 *
 * <p>Malformed input - truncated structures, unterminated strings, trailing
 * garbage after the top-level value, anything that isn't valid JSON - throws
 * {@link JsonException} with a message naming the character position and
 * (where there is one) the offending character, so a caller staring at a bad
 * payload has something to go on.
 */
final class JsonParser {

    /** The JSON text being parsed. */
    private final String json;

    /** The index of the next character to read. */
    private int pos;

    private JsonParser(String json) {
        this.json = json;
    }

    /**
     * Parses {@code json} as a single JSON value, ignoring leading/trailing
     * whitespace around it.
     *
     * @throws JsonException if {@code json} is not valid JSON, or has
     *                        non-whitespace content after the top-level value
     */
    static Object parse(String json) {
        JsonParser parser = new JsonParser(json);
        parser.skipWhitespace();
        Object value = parser.parseValue();
        parser.skipWhitespace();

        if (parser.pos != json.length()) {
            throw parser.errorAt("trailing content after JSON value", parser.pos);
        }
        return value;
    }

    private Object parseValue() {
        if (pos >= json.length()) {
            throw errorAt("unexpected end of input", pos);
        }

        char c = json.charAt(pos);
        return switch (c) {
            case '{' -> parseObject();
            case '[' -> parseArray();
            case '"' -> parseString();
            case 't', 'f' -> parseBoolean();
            case 'n' -> parseNull();
            default -> {
                if (c == '-' || Character.isDigit(c)) {
                    yield parseNumber();
                }
                throw errorAt("unexpected character '" + c + "'", pos);
            }
        };
    }

    private Map<String, Object> parseObject() {
        expect('{');
        // LinkedHashMap preserves the source's key order, which matters for
        // a faithful round-trip of things like UnknownAuditEvent.rawJson.
        Map<String, Object> result = new LinkedHashMap<>();

        skipWhitespace();
        if (peek() == '}') {
            pos++;
            return result;
        }

        while (true) {
            skipWhitespace();
            if (peek() != '"') {
                throw errorAt("expected a string key", pos);
            }
            String key = parseString();

            skipWhitespace();
            expect(':');
            skipWhitespace();

            result.put(key, parseValue());

            skipWhitespace();
            char next = peek();
            if (next == ',') {
                pos++;
            } else if (next == '}') {
                pos++;
                return result;
            } else {
                throw errorAt("expected ',' or '}'", pos);
            }
        }
    }

    private List<Object> parseArray() {
        expect('[');
        List<Object> result = new ArrayList<>();

        skipWhitespace();
        if (peek() == ']') {
            pos++;
            return result;
        }

        while (true) {
            skipWhitespace();
            result.add(parseValue());

            skipWhitespace();
            char next = peek();
            if (next == ',') {
                pos++;
            } else if (next == ']') {
                pos++;
                return result;
            } else {
                throw errorAt("expected ',' or ']'", pos);
            }
        }
    }

    private String parseString() {
        expect('"');
        StringBuilder result = new StringBuilder();

        while (true) {
            if (pos >= json.length()) {
                throw errorAt("unterminated string", pos);
            }
            char c = json.charAt(pos++);

            if (c == '"') {
                return result.toString();
            }
            if (c == '\\') {
                result.append(parseEscape());
            } else if (c < 0x20) {
                // Raw control characters are not legal inside a JSON string
                // literal - they must be escaped by the producer.
                throw errorAt("unescaped control character in string", pos - 1);
            } else {
                result.append(c);
            }
        }
    }

    private char parseEscape() {
        if (pos >= json.length()) {
            throw errorAt("unterminated escape sequence", pos);
        }
        char escaped = json.charAt(pos++);
        return switch (escaped) {
            case '"' -> '"';
            case '\\' -> '\\';
            case '/' -> '/';
            case 'n' -> '\n';
            case 'r' -> '\r';
            case 't' -> '\t';
            case 'b' -> '\b';
            case 'f' -> '\f';
            case 'u' -> parseUnicodeEscape();
            default -> throw errorAt("invalid escape sequence '\\" + escaped + "'", pos - 1);
        };
    }

    private char parseUnicodeEscape() {
        if (pos + 4 > json.length()) {
            throw errorAt("truncated \\u escape", pos);
        }
        String hex = json.substring(pos, pos + 4);
        try {
            char value = (char) Integer.parseInt(hex, 16);
            pos += 4;
            return value;
        } catch (NumberFormatException e) {
            throw errorAt("invalid \\u escape '" + hex + "'", pos);
        }
    }

    private Boolean parseBoolean() {
        if (json.startsWith("true", pos)) {
            pos += 4;
            return Boolean.TRUE;
        }
        if (json.startsWith("false", pos)) {
            pos += 5;
            return Boolean.FALSE;
        }
        throw errorAt("invalid literal (expected 'true' or 'false')", pos);
    }

    private Object parseNull() {
        if (json.startsWith("null", pos)) {
            pos += 4;
            return null;
        }
        throw errorAt("invalid literal (expected 'null')", pos);
    }

    /**
     * Parses a JSON number. Integers (no {@code .}, {@code e}, or {@code E})
     * become {@code Long}; anything with a fractional part or exponent
     * becomes {@code Double}. This mirrors the split JSON itself doesn't
     * make (JSON has one "number" type) but Java requires, and matches the
     * types {@link JsonWriter} accepts back on the way out.
     */
    private Object parseNumber() {
        int start = pos;
        boolean isFloatingPoint = false;

        if (peek() == '-') {
            pos++;
        }
        while (pos < json.length() && Character.isDigit(json.charAt(pos))) {
            pos++;
        }
        if (pos < json.length() && json.charAt(pos) == '.') {
            isFloatingPoint = true;
            pos++;
            while (pos < json.length() && Character.isDigit(json.charAt(pos))) {
                pos++;
            }
        }
        if (pos < json.length() && (json.charAt(pos) == 'e' || json.charAt(pos) == 'E')) {
            isFloatingPoint = true;
            pos++;
            if (pos < json.length() && (json.charAt(pos) == '+' || json.charAt(pos) == '-')) {
                pos++;
            }
            while (pos < json.length() && Character.isDigit(json.charAt(pos))) {
                pos++;
            }
        }

        String text = json.substring(start, pos);
        if (text.isEmpty() || "-".equals(text)) {
            throw errorAt("invalid number", start);
        }

        try {
            return isFloatingPoint ? (Object) Double.parseDouble(text) : (Object) Long.parseLong(text);
        } catch (NumberFormatException e) {
            throw errorAt("invalid number '" + text + "'", start);
        }
    }

    private void skipWhitespace() {
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
            pos++;
        }
    }

    private char peek() {
        if (pos >= json.length()) {
            throw errorAt("unexpected end of input", pos);
        }
        return json.charAt(pos);
    }

    private void expect(char c) {
        if (pos >= json.length() || json.charAt(pos) != c) {
            throw errorAt("expected '" + c + "'", pos);
        }
        pos++;
    }

    private JsonException errorAt(String message, int position) {
        String context =
                (position < json.length())
                        ? "'" + json.charAt(position) + "'"
                        : "<end of input>";
        return new JsonException(message + " at position " + position + " (" + context + ")");
    }
}
