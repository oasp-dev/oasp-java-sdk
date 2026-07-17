package dev.oasp.client.json;

import java.util.List;
import java.util.Map;

/**
 * Renders JSON's own generic value model - {@code Map<String,Object>} for
 * objects, {@code List<Object>} for arrays, {@code String}, {@code Number},
 * {@code Boolean}, and {@code null} - to JSON text.
 *
 * <p>This class has no knowledge of {@code dev.oasp.client.types}; mapping a
 * protocol type (e.g. {@link dev.oasp.client.types.Conversation}) to this
 * generic tree is {@link HandRolledJsonCodec}'s job. Keeping the two
 * separate means the fiddly part - correct string escaping - can be tested
 * in isolation, against plain values, with no protocol types involved.
 *
 * <p>Not meant to be instantiated: every method here is a pure function of
 * its argument, so there's no instance state to hold.
 */
final class JsonWriter {

    private JsonWriter() {}

    /**
     * Renders a single value as JSON text.
     *
     * @param value a {@code String}, {@code Boolean}, {@code Long}/{@code
     *              Integer}/{@code Double} (or another boxed {@link
     *              Number}), {@code Map<String,Object>}, {@code
     *              List<Object>}, or {@code null}
     * @return the JSON text for {@code value}
     * @throws JsonException if {@code value} is of a type this writer
     *                        doesn't know how to render
     */
    static String write(Object value) {
        StringBuilder out = new StringBuilder();
        writeValue(value, out);
        return out.toString();
    }

    private static void writeValue(Object value, StringBuilder out) {
        switch (value) {
            case null -> out.append("null");
            case String s -> writeString(s, out);
            case Boolean b -> out.append(b.booleanValue());
            case Map<?, ?> map -> writeObject(map, out);
            case List<?> list -> writeArray(list, out);

            // Long/Integer/Short/Byte all render as plain integer literals;
            // Double/Float as JSON's decimal/exponent number syntax, which
            // is exactly what Double.toString()/Float.toString() produce
            // for every finite value. NaN and infinities have no JSON
            // representation, so they're rejected rather than silently
            // emitting invalid JSON.
            case Double d -> writeFiniteNumber(d, d.isNaN() || d.isInfinite(), out);
            case Float f -> writeFiniteNumber(f, f.isNaN() || f.isInfinite(), out);
            case Number n -> out.append(n);

            default ->
                    throw new JsonException(
                            "Cannot write value of unsupported type: " + value.getClass().getName());
        }
    }

    private static void writeFiniteNumber(Number n, boolean nonFinite, StringBuilder out) {
        if (nonFinite) {
            throw new JsonException("Cannot write non-finite number as JSON: " + n);
        }
        out.append(n);
    }

    private static void writeObject(Map<?, ?> map, StringBuilder out) {
        out.append('{');
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                out.append(',');
            }
            first = false;

            Object key = entry.getKey();
            if (!(key instanceof String keyString)) {
                throw new JsonException(
                        "JSON object keys must be strings, got: "
                                + (key == null ? "null" : key.getClass().getName()));
            }
            writeString(keyString, out);
            out.append(':');
            writeValue(entry.getValue(), out);
        }
        out.append('}');
    }

    private static void writeArray(List<?> list, StringBuilder out) {
        out.append('[');
        boolean first = true;
        for (Object element : list) {
            if (!first) {
                out.append(',');
            }
            first = false;
            writeValue(element, out);
        }
        out.append(']');
    }

    /**
     * Writes {@code s} as a JSON string literal, including the surrounding
     * quotes and all required escaping.
     *
     * <p>Escaped: {@code "} and {@code \} (structurally required), plus the
     * named two-character escapes JSON defines for the common control
     * characters ({@code \n \r \t \b \f}). Any other control character (
     * {@code U+0000}-{@code U+001F}) falls back to a generic four-hex-digit
     * unicode escape, since JSON forbids raw control characters in a string
     * literal.
     * {@code /} is deliberately left unescaped: {@code \/} is a JSON-legal
     * escape, but escaping it is optional and this writer doesn't bother -
     * unescaped {@code /} is both valid and more readable.
     */
    private static void writeString(String s, StringBuilder out) {
        out.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                case '\b' -> out.append("\\b");
                case '\f' -> out.append("\\f");
                default -> {
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        out.append('"');
    }
}
