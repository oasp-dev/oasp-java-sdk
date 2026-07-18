package dev.oasp.client.json;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * The single API for reading a named field out of a parsed JSON object and
 * coercing it to the wanted type in ONE call, with the field name written
 * once. Builds on {@link JsonTrees} for node shape and converts scalar values
 * (Instant, enum) itself, so the type mappers never touch the lower layers
 * directly.
 *
 * <p>Every {@code optionalX}/{@code xOrEmpty} method treats a missing key and
 * an explicit JSON {@code null} the same way - both mean "absent" - matching
 * every {@code Optional}-typed record component's compact-constructor
 * normalization elsewhere in this SDK.
 */
final class JsonFields {

    private JsonFields() {}

    static String string(Map<String, Object> obj, String name) {
        return JsonTrees.asString(JsonTrees.field(obj, name), name);
    }

    static Map<String, Object> object(Map<String, Object> obj, String name) {
        return JsonTrees.asObject(JsonTrees.field(obj, name), name);
    }

    static List<Object> array(Map<String, Object> obj, String name) {
        return JsonTrees.asArray(JsonTrees.field(obj, name), name);
    }

    /** Like {@link #array(Map, String)}, but a missing/null field is an empty list rather than an error. */
    static List<Object> arrayOrEmpty(Map<String, Object> obj, String name) {
        Object node = JsonTrees.field(obj, name);
        return node == null ? List.of() : JsonTrees.asArray(node, name);
    }

    static long longValue(Map<String, Object> obj, String name) {
        return JsonTrees.asLong(JsonTrees.field(obj, name), name);
    }

    static boolean bool(Map<String, Object> obj, String name) {
        return JsonTrees.asBoolean(JsonTrees.field(obj, name), name);
    }

    static Instant instant(Map<String, Object> obj, String name) {
        return instant(string(obj, name));
    }

    static Optional<String> optionalString(Map<String, Object> obj, String name) {
        return optional(obj, name, JsonTrees::asString);
    }

    static Optional<Boolean> optionalBoolean(Map<String, Object> obj, String name) {
        return optional(obj, name, JsonTrees::asBoolean);
    }

    static Optional<Map<String, Object>> optionalObject(Map<String, Object> obj, String name) {
        return optional(obj, name, JsonTrees::asObject);
    }

    /** A named field coerced with {@code coerce}, or empty when the field is missing or JSON null. */
    private static <T> Optional<T> optional(
            Map<String, Object> obj, String name, BiFunction<Object, String, T> coerce) {
        Object node = JsonTrees.field(obj, name);
        return node == null ? Optional.empty() : Optional.of(coerce.apply(node, name));
    }

    /**
     * Reads a named field as a string and converts it with {@code fromWire}
     * (e.g. {@code PrincipalKind::fromWire}) - the strict direction of every
     * protocol enum's wire mapping. {@code fromWire} is expected to throw
     * {@link JsonException} on a value it doesn't recognise; that exception
     * propagates unchanged.
     */
    static <E extends Enum<E>> E enumValue(Map<String, Object> obj, String name, Function<String, E> fromWire) {
        return fromWire.apply(string(obj, name));
    }

    // Scalar converters, also used directly for bare document-root reads.

    static Instant instant(String text) {
        try {
            return Instant.parse(text);
        } catch (DateTimeParseException e) {
            throw new JsonException("Invalid ISO-8601 instant: \"" + text + "\"", e);
        }
    }
}
