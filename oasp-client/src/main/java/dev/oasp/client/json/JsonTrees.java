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

    static Map<String, Object> asObject(Object node, String field) {
        if (!(node instanceof Map<?, ?> map)) {
            throw new JsonException(
                    "Expected \"" + field + "\" to be a JSON object but was " + describe(node));
        }
        return castObjectMap(map);
    }

    static List<Object> asArray(Object node, String field) {
        if (!(node instanceof List<?> list)) {
            throw new JsonException(
                    "Expected \"" + field + "\" to be a JSON array but was " + describe(node));
        }
        return castObjectList(list);
    }

    static String asString(Object node, String field) {
        if (!(node instanceof String s)) {
            throw new JsonException(
                    "Expected \"" + field + "\" to be a JSON string but was " + describe(node));
        }
        return s;
    }

    static long asLong(Object node, String field) {
        if (!(node instanceof Long l)) {
            throw new JsonException(
                    "Expected \"" + field + "\" to be a JSON integer but was " + describe(node));
        }
        return l;
    }

    static boolean asBoolean(Object node, String field) {
        if (!(node instanceof Boolean b)) {
            throw new JsonException(
                    "Expected \"" + field + "\" to be a JSON boolean but was " + describe(node));
        }
        return b;
    }

    // The two casts below are unverifiable under type erasure (the compiler
    // confirms Map/List but not the type arguments), so each is quarantined in
    // its own method where @SuppressWarnings covers nothing but the cast. Safe
    // because the tree comes only from JsonParser, which builds Map<String,Object>
    // for every object and List<Object> for every array.
    @SuppressWarnings("unchecked")
    private static Map<String, Object> castObjectMap(Map<?, ?> map) {
        return (Map<String, Object>) map;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> castObjectList(List<?> list) {
        return (List<Object>) list;
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
