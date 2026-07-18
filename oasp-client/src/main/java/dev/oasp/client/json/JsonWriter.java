package dev.oasp.client.json;

import java.util.List;
import java.util.Map;

/**
 * Renders JSON's own generic value model - {@code Map<String,Object>} for
 * objects, {@code List<Object>} for arrays, {@code String}, {@code Number},
 * {@code Boolean}, and {@code null} - to JSON text.
 *
 * <p>This class has no knowledge of {@code dev.oasp.client.types}; mapping a
 * protocol type to this generic tree is {@link HandRolledJsonCodec}'s job.
 * String escaping lives in {@link JsonStringEscaper}, object/array rendering
 * in {@link JsonContainers}, so the fiddly parts can be tested in isolation.
 *
 * <p>Not meant to be instantiated: every method here is a pure function of
 * its argument, so there's no instance state to hold.
 */
final class JsonWriter {

    private JsonWriter() {}

    /**
     * Renders a single value as JSON text.
     *
     * @throws JsonException if {@code value} is of a type this writer
     *                        doesn't know how to render
     */
    static String write(Object value) {
        StringBuilder out = new StringBuilder();
        writeValue(value, out);
        return out.toString();
    }

    static void writeValue(Object value, StringBuilder out) {
        switch (value) {
            case null -> out.append("null");
            case String s -> JsonStringEscaper.writeString(s, out);
            case Boolean b -> out.append(b.booleanValue());
            case Map<?, ?> map -> JsonContainers.writeObject(map, out);
            case List<?> list -> JsonContainers.writeArray(list, out);

            // Long/Integer/Short/Byte render as plain integer literals;
            // Double/Float as JSON's decimal/exponent syntax, exactly what
            // Double.toString()/Float.toString() produce for finite values.
            // NaN/infinity have no JSON representation, so are rejected.
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
}
