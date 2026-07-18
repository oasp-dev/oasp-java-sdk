package dev.oasp.client.json;

import dev.oasp.client.types.UnknownEvent;
import dev.oasp.client.types.UnknownResource;
import java.util.Objects;

/**
 * The default, zero-dependency {@link JsonCodec}: maps every type in {@code
 * dev.oasp.client.types} to/from JSON by hand, on top of {@link JsonWriter}
 * and {@link JsonParser}.
 *
 * <p>This does NOT attempt a generic, reflection-based mapper. The set of
 * types that need mapping is small and closed, so writing one explicit case
 * per type (see {@link ProtocolTreeWriter}/{@link ProtocolTreeReader}) is
 * both simpler to follow and safer than reflection.
 *
 * <p>The JSON field names used throughout this package are the corresponding
 * record component names, verbatim, matching oasp-standard's vendored JSON
 * Schemas under {@code src/test/resources/spec-schemas/} - see {@code
 * SpecDriftTest} in {@code dev.oasp.client.types} and this package's own
 * spec-shape tests.
 */
final class HandRolledJsonCodec implements JsonCodec {

    @Override
    public String write(Object value) {
        // UnknownResource/UnknownEvent are deliberate special cases: each is
        // written by emitting its preserved rawJson verbatim rather than
        // rebuilding JSON text from a tree, so a round-trip of a resource or
        // event this SDK version doesn't recognise is byte-for-byte
        // faithful to whatever the server actually sent.
        if (value instanceof UnknownResource unknown) {
            return unknown.rawJson();
        }
        if (value instanceof UnknownEvent unknown) {
            return unknown.rawJson();
        }
        return JsonWriter.write(ProtocolTreeWriter.toTree(value));
    }

    @Override
    public <T> T read(String json, Class<T> type) {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        // Parsing (JSON text -> generic tree) is JsonParser's job and
        // already throws JsonException on malformed input; let that
        // propagate as-is.
        Object tree = JsonParser.parse(json);

        try {
            return type.cast(ProtocolTreeReader.fromTree(tree, type, json));
        } catch (JsonException e) {
            // Already the right exception type - don't double-wrap it.
            throw e;
        } catch (RuntimeException e) {
            // Anything else unexpected during mapping - most commonly a
            // record's compact constructor rejecting a value we handed it,
            // or a ClassCastException from type.cast() above - is wrapped
            // as a JsonException so every failure from read() is one
            // consistent, documented exception type.
            throw new JsonException(
                    "Failed to read JSON as " + type.getSimpleName() + ": " + e.getMessage(), e);
        }
    }
}
