package dev.oasp.client.json;

import java.util.List;
import java.util.Map;

/**
 * Small helpers for pulling typed values out of the generic JSON tree, each
 * throwing a {@link JsonException} naming the field on a shape mismatch
 * instead of letting a raw {@code ClassCastException} leak out.
 */
final class JsonTrees {

    private JsonTrees() {}

    static Object field(Map<String, Object> obj, String name) {
        return obj.get(name);
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> asObject(Object node, String field) {
        if (!(node instanceof Map<?, ?> map)) {
            throw new JsonException(
                    "Expected \"" + field + "\" to be a JSON object but was " + describe(node));
        }
        return (Map<String, Object>) map;
    }

    @SuppressWarnings("unchecked")
    static List<Object> asArray(Object node, String field) {
        if (!(node instanceof List<?> list)) {
            throw new JsonException(
                    "Expected \"" + field + "\" to be a JSON array but was " + describe(node));
        }
        return (List<Object>) list;
    }

    static String asString(Object node, String field) {
        if (!(node instanceof String s)) {
            throw new JsonException(
                    "Expected \"" + field + "\" to be a JSON string but was " + describe(node));
        }
        return s;
    }

    static String describe(Object node) {
        if (node == null) {
            return "null";
        }
        if (node instanceof Map) {
            return "an object";
        }
        if (node instanceof List) {
            return "an array";
        }
        if (node instanceof String) {
            return "a string";
        }
        if (node instanceof Boolean) {
            return "a boolean";
        }
        if (node instanceof Number) {
            return "a number";
        }
        return node.getClass().getName();
    }
}
