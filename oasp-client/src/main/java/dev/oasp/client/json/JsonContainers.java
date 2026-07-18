package dev.oasp.client.json;

import java.util.List;
import java.util.Map;

/** Writes JSON objects and arrays, recursing into {@link JsonWriter#writeValue} for elements. */
final class JsonContainers {

    private JsonContainers() {}

    static void writeObject(Map<?, ?> map, StringBuilder out) {
        out.append('{');
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                out.append(',');
            }
            first = false;

            Object key = entry.getKey();
            if (!(key instanceof String keyString)) {
                throw new JsonException(
                        "JSON object keys must be strings, got: "
                                + (key == null ? "null" : key.getClass().getName()));
            }
            JsonStringEscaper.writeString(keyString, out);
            out.append(':');
            JsonWriter.writeValue(entry.getValue(), out);
        }
        out.append('}');
    }

    static void writeArray(List<?> list, StringBuilder out) {
        out.append('[');
        boolean first = true;
        for (Object element : list) {
            if (!first) {
                out.append(',');
            }
            first = false;
            JsonWriter.writeValue(element, out);
        }
        out.append(']');
    }
}
