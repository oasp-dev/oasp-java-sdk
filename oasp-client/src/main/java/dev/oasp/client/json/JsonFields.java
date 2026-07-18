package dev.oasp.client.json;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Reads a named field from a parsed JSON object and coerces it to the wanted
 * type in one call. The point is that the field name is written ONCE per read,
 * instead of being repeated across a {@link JsonTrees#field}+{@code asX} pair
 * (where a copy-paste slip could name the wrong field in an error message).
 * Combines {@link JsonTrees} (node shape) with {@link ValueMappers} (value
 * conversion) so call sites read as intent, not plumbing.
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

    static Instant instant(Map<String, Object> obj, String name) {
        return ValueMappers.mapInstant(string(obj, name));
    }

    static Optional<Instant> optionalInstant(Map<String, Object> obj, String name) {
        return ValueMappers.mapOptionalInstant(JsonTrees.field(obj, name));
    }

    static <E extends Enum<E>> E enumValue(Class<E> type, Map<String, Object> obj, String name) {
        return ValueMappers.mapEnum(type, string(obj, name));
    }
}
