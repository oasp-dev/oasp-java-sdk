package dev.oasp.client.json;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The single API for reading a named field out of a parsed JSON object and
 * coercing it to the wanted type in ONE call, with the field name written
 * once. Builds on {@link JsonTrees} for node shape and converts scalar values
 * (Instant, enum) itself, so the type mappers never touch the lower layers
 * directly.
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
        return instant(string(obj, name));
    }

    /** A null/absent field is Optional.empty() (matches Conversation's null-&gt;empty). */
    static Optional<Instant> optionalInstant(Map<String, Object> obj, String name) {
        Object node = JsonTrees.field(obj, name);
        return node == null ? Optional.empty() : Optional.of(instant(JsonTrees.asString(node, name)));
    }

    static <E extends Enum<E>> E enumValue(Class<E> type, Map<String, Object> obj, String name) {
        return enumValue(type, string(obj, name));
    }

    // Scalar converters, also used directly for bare document-root reads.

    static Instant instant(String text) {
        try {
            return Instant.parse(text);
        } catch (DateTimeParseException e) {
            throw new JsonException("Invalid ISO-8601 instant: \"" + text + "\"", e);
        }
    }

    /**
     * Enum lookup by exact name(). Strict: only AuditEvent's {@code type}
     * discriminator gets an unknown-value fallback (UnknownAuditEvent); every
     * other enum here is closed, so an unrecognised value is a genuine error.
     */
    static <E extends Enum<E>> E enumValue(Class<E> type, String name) {
        try {
            return Enum.valueOf(type, name);
        } catch (IllegalArgumentException e) {
            throw new JsonException("Unrecognised " + type.getSimpleName() + " value: \"" + name + "\"", e);
        }
    }
}
