package dev.oasp.client.json;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/** Maps generic tree values (strings, nullable fields) onto small typed values. */
final class ValueMappers {

    private ValueMappers() {}

    static Instant mapInstant(String text) {
        try {
            return Instant.parse(text);
        } catch (DateTimeParseException e) {
            throw new JsonException("Invalid ISO-8601 instant: \"" + text + "\"", e);
        }
    }

    static Optional<Instant> mapOptionalInstant(Object node) {
        // A null/absent closedAt (Map.get() returns null either way) means
        // "not closed yet" - matches Conversation's own normalization of a
        // null closedAt to Optional.empty() in its compact constructor.
        if (node == null) {
            return Optional.empty();
        }
        return Optional.of(mapInstant(JsonTrees.asString(node, "closedAt")));
    }

    /**
     * Looks up an enum constant by exact {@code name()} match. Deliberately
     * strict: only {@code AuditEvent}'s {@code type} discriminator gets an
     * unknown-value fallback ({@code UnknownAuditEvent}); every other enum
     * here is closed and an unrecognised value is a genuine error.
     */
    static <E extends Enum<E>> E mapEnum(Class<E> enumType, String name) {
        try {
            return Enum.valueOf(enumType, name);
        } catch (IllegalArgumentException e) {
            throw new JsonException(
                    "Unrecognised " + enumType.getSimpleName() + " value: \"" + name + "\"", e);
        }
    }
}
