package dev.oasp.client.json;

/**
 * A small recursive-descent parser that turns JSON text into JSON's own
 * generic value model: object -> {@code Map<String,Object>} (key order
 * preserved), array -> {@code List<Object>}, string -> {@code String},
 * integer -> {@code Long}, decimal/exponent number -> {@code Double}, {@code
 * true}/{@code false} -> {@code Boolean}, {@code null} -> Java {@code null}.
 *
 * <p>This class has no knowledge of {@code dev.oasp.client.types}; mapping
 * the tree it produces to a protocol type is {@link HandRolledJsonCodec}'s
 * job. Malformed input throws {@link JsonException} naming the character
 * position and offending character, via {@link JsonReader}.
 *
 * <p>The recursive structural core lives in {@link JsonStructures}; leaf
 * scalars in {@link JsonScalars} and {@link JsonStrings}.
 */
final class JsonParser {

    private JsonParser() {}

    /**
     * Parses {@code json} as a single JSON value, ignoring leading/trailing
     * whitespace around it.
     *
     * @throws JsonException if {@code json} is not valid JSON, or has
     *                        non-whitespace content after the top-level value
     */
    static Object parse(String json) {
        JsonReader reader = new JsonReader(json);
        reader.skipWhitespace();
        Object value = parseValue(reader);
        reader.skipWhitespace();

        if (reader.hasNext()) {
            throw reader.error("trailing content after JSON value");
        }
        return value;
    }

    static Object parseValue(JsonReader reader) {
        if (!reader.hasNext()) {
            throw reader.error("unexpected end of input");
        }

        char c = reader.peek();
        return switch (c) {
            case '{' -> JsonStructures.parseObject(reader);
            case '[' -> JsonStructures.parseArray(reader);
            case '"' -> JsonStrings.parseString(reader);
            case 't', 'f' -> JsonScalars.parseBoolean(reader);
            case 'n' -> JsonScalars.parseNull(reader);
            default -> {
                if (c == '-' || Character.isDigit(c)) {
                    yield JsonScalars.parseNumber(reader);
                }
                throw reader.error("unexpected character '" + c + "'");
            }
        };
    }
}
