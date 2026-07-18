package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Writing non-string values: null, boolean, numbers, objects, and arrays. */
class JsonWriterValueTest {

    @Test
    void writesNull() {
        assertThat(JsonWriter.write(null)).isEqualTo("null");
    }

    @Test
    void writesBooleans() {
        assertThat(JsonWriter.write(true)).isEqualTo("true");
        assertThat(JsonWriter.write(false)).isEqualTo("false");
    }

    @Test
    void writesIntegerNumbers() {
        assertThat(JsonWriter.write(42L)).isEqualTo("42");
        assertThat(JsonWriter.write(-7L)).isEqualTo("-7");
    }

    @Test
    void writesDecimalNumbers() {
        assertThat(JsonWriter.write(3.14)).isEqualTo("3.14");
    }

    @Test
    void rejectsNonFiniteNumbers() {
        assertThatThrownBy(() -> JsonWriter.write(Double.NaN)).isInstanceOf(JsonException.class);
        assertThatThrownBy(() -> JsonWriter.write(Double.POSITIVE_INFINITY))
                .isInstanceOf(JsonException.class);
    }

    @Test
    void writesEmptyObjectAndArray() {
        assertThat(JsonWriter.write(Map.of())).isEqualTo("{}");
        assertThat(JsonWriter.write(List.of())).isEqualTo("[]");
    }

    @Test
    void writesNestedObjectPreservingInsertionOrder() {
        Map<String, Object> inner = new LinkedHashMap<>();
        inner.put("id", "abc");
        inner.put("count", 3L);

        Map<String, Object> outer = new LinkedHashMap<>();
        outer.put("name", "test");
        outer.put("nested", inner);
        outer.put("tags", List.of("a", "b"));

        assertThat(JsonWriter.write(outer))
                .isEqualTo("{\"name\":\"test\",\"nested\":{\"id\":\"abc\",\"count\":3},\"tags\":[\"a\",\"b\"]}");
    }

    @Test
    void rejectsNonStringMapKeys() {
        Map<Object, Object> badMap = new LinkedHashMap<>();
        badMap.put(1, "value");

        assertThatThrownBy(() -> JsonWriter.write(badMap)).isInstanceOf(JsonException.class);
    }

    @Test
    void rejectsUnsupportedValueType() {
        assertThatThrownBy(() -> JsonWriter.write(new Object())).isInstanceOf(JsonException.class);
    }
}
