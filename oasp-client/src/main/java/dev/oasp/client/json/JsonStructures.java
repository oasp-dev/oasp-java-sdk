package dev.oasp.client.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Parses JSON objects and arrays, recursing into {@link JsonParser#parseValue} for elements. */
final class JsonStructures {

    private JsonStructures() {}

    static Map<String, Object> parseObject(JsonReader reader) {
        reader.expect('{');
        // LinkedHashMap preserves the source's key order, which matters for
        // a faithful round-trip of things like UnknownAuditEvent.rawJson.
        Map<String, Object> result = new LinkedHashMap<>();

        reader.skipWhitespace();
        if (reader.peek() == '}') {
            reader.next();
            return result;
        }

        while (true) {
            reader.skipWhitespace();
            if (reader.peek() != '"') {
                throw reader.error("expected a string key");
            }
            String key = JsonStrings.parseString(reader);

            reader.skipWhitespace();
            reader.expect(':');
            reader.skipWhitespace();

            result.put(key, JsonParser.parseValue(reader));

            reader.skipWhitespace();
            char next = reader.peek();
            if (next == ',') {
                reader.next();
            } else if (next == '}') {
                reader.next();
                return result;
            } else {
                throw reader.error("expected ',' or '}'");
            }
        }
    }

    static List<Object> parseArray(JsonReader reader) {
        reader.expect('[');
        List<Object> result = new ArrayList<>();

        reader.skipWhitespace();
        if (reader.peek() == ']') {
            reader.next();
            return result;
        }

        while (true) {
            reader.skipWhitespace();
            result.add(JsonParser.parseValue(reader));

            reader.skipWhitespace();
            char next = reader.peek();
            if (next == ',') {
                reader.next();
            } else if (next == ']') {
                reader.next();
                return result;
            } else {
                throw reader.error("expected ',' or ']'");
            }
        }
    }
}
