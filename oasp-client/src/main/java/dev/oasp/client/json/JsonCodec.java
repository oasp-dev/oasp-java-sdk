package dev.oasp.client.json;

import java.util.ServiceLoader;

/**
 * Converts between JSON text and the protocol types in {@code
 * dev.oasp.client.types}.
 *
 * <p>This is a small SPI (service provider interface), not just a class:
 * callers of this SDK are expected to go through {@link #getDefault()}
 * rather than depend on {@link HandRolledJsonCodec} directly. That
 * indirection is the whole point - see {@link #getDefault()}.
 */
public interface JsonCodec {

    /**
     * Serialises a protocol type instance (e.g. a {@link
     * dev.oasp.client.types.Conversation}) to its JSON text.
     *
     * @throws JsonException if {@code value} is not a supported type
     */
    String write(Object value);

    /**
     * Parses JSON text back into an instance of the requested protocol
     * type.
     *
     * @param json the JSON text to parse
     * @param type the protocol type to map the parsed JSON onto, e.g.
     *             {@code Conversation.class}
     * @throws JsonException if {@code json} is not valid JSON, does not have
     *                        the shape {@code type} requires, or {@code
     *                        type} is not supported
     */
    <T> T read(String json, Class<T> type);

    /**
     * Returns the {@link JsonCodec} this SDK should use.
     *
     * <p>This is a {@link ServiceLoader} seam: if some other jar on the
     * classpath registers a {@code JsonCodec} provider (for example, a
     * future Jackson-backed module - see DECISIONS.md), that provider wins.
     * Only when no provider is registered does this fall back to {@link
     * HandRolledJsonCodec}, the zero-dependency implementation built in this
     * package.
     *
     * <p>{@link HandRolledJsonCodec} is deliberately NOT registered as a
     * {@code ServiceLoader} provider of its own (there is no {@code
     * META-INF/services/dev.oasp.client.json.JsonCodec} entry naming it) -
     * that's what keeps it the fallback rather than one more competing
     * provider. This seam is exactly what would let a Jackson-backed codec
     * be dropped in later, purely as an additional jar on the classpath,
     * with zero change to this SDK's public API: every call site already
     * goes through {@code JsonCodec}, never through {@code
     * HandRolledJsonCodec} directly.
     */
    static JsonCodec getDefault() {
        return ServiceLoader.load(JsonCodec.class).findFirst().orElseGet(HandRolledJsonCodec::new);
    }
}
