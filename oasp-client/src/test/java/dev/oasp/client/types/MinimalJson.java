package dev.oasp.client.types;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A minimal, dependency-free JSON reader for test-only use by {@link
 * SpecDriftTest}. {@code oasp-client}'s zero-runtime-dependency rule only
 * binds the published jar; test code may use libraries freely, but the
 * vendored spec schemas under {@code src/test/resources/spec-schemas} are
 * small, well-formed JSON Schema documents, so a compact hand-rolled reader
 * is simpler than pulling in a JSON library just for this one test.
 *
 * <p>Parses into plain JDK types: {@code Map<String,Object>}, {@code
 * List<Object>}, {@code String}, {@code Boolean}, {@code Double}, or {@code
 * null} - enough structure for {@link SpecDriftTest} to read {@code
 * properties}, {@code required}, and {@code const} out of a schema.
 */
final class MinimalJson {

    private final String text;
    private int pos;

    private MinimalJson(String text) {
        this.text = text;
    }

    static Object parse(String json) {
        MinimalJson reader = new MinimalJson(json);
        Object value = reader.readValue();
        reader.skipWhitespace();
        return value;
    }

    private Object readValue() {
        skipWhitespace();
        char c = text.charAt(pos);
        return switch (c) {
            case '{' -> readObject();
            case '[' -> readArray();
            case '"' -> readString();
            case 't' -> readLiteral("true", Boolean.TRUE);
            case 'f' -> readLiteral("false", Boolean.FALSE);
            case 'n' -> readLiteral("null", null);
            default -> readNumber();
        };
    }

    private Map<String, Object> readObject() {
        Map<String, Object> result = new LinkedHashMap<>();
        pos++; // '{'
        skipWhitespace();
        if (text.charAt(pos) == '}') {
            pos++;
            return result;
        }
        while (true) {
            skipWhitespace();
            String key = readString();
            skipWhitespace();
            pos++; // ':'
            result.put(key, readValue());
            skipWhitespace();
            char next = text.charAt(pos++);
            if (next == '}') {
                return result;
            }
        }
    }

    private List<Object> readArray() {
        List<Object> result = new ArrayList<>();
        pos++; // '['
        skipWhitespace();
        if (text.charAt(pos) == ']') {
            pos++;
            return result;
        }
        while (true) {
            result.add(readValue());
            skipWhitespace();
            char next = text.charAt(pos++);
            if (next == ']') {
                return result;
            }
        }
    }

    private String readString() {
        pos++; // opening quote
        StringBuilder builder = new StringBuilder();
        while (true) {
            char c = text.charAt(pos++);
            if (c == '"') {
                return builder.toString();
            }
            if (c == '\\') {
                char escaped = text.charAt(pos++);
                builder.append(switch (escaped) {
                    case 'n' -> '\n';
                    case 't' -> '\t';
                    case 'r' -> '\r';
                    case 'u' -> readUnicodeEscape();
                    default -> escaped;
                });
            } else {
                builder.append(c);
            }
        }
    }

    private char readUnicodeEscape() {
        char value = (char) Integer.parseInt(text.substring(pos, pos + 4), 16);
        pos += 4;
        return value;
    }

    private Object readLiteral(String literal, Object value) {
        pos += literal.length();
        return value;
    }

    private Double readNumber() {
        int start = pos;
        while (pos < text.length() && "-+.eE0123456789".indexOf(text.charAt(pos)) >= 0) {
            pos++;
        }
        return Double.parseDouble(text.substring(start, pos));
    }

    private void skipWhitespace() {
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
    }
}
