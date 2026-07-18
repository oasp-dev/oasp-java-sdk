package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.oasp.client.types.Principal;
import dev.oasp.client.types.Resource;
import org.junit.jupiter.api.Test;

/** Malformed/wrong-shape/unsupported-type input at the codec level. */
class MalformedJsonTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

    @Test
    void readingMalformedJsonThrowsJsonException() {
        assertThatThrownBy(() -> codec.read("{\"id\":", Principal.class)).isInstanceOf(JsonException.class);
    }

    @Test
    void readingUnterminatedStringThrowsJsonException() {
        assertThatThrownBy(() -> codec.read("{\"id\": \"unterminated", Principal.class))
                .isInstanceOf(JsonException.class);
    }

    @Test
    void readingWrongShapeThrowsJsonException() {
        // A JSON array where an object is required.
        assertThatThrownBy(() -> codec.read("[1,2,3]", Principal.class)).isInstanceOf(JsonException.class);
    }

    @Test
    void readingAResourceWithNoResourceTypeFieldThrowsJsonException() {
        assertThatThrownBy(() -> codec.read("{\"id\":\"x\"}", Resource.class)).isInstanceOf(JsonException.class);
    }

    @Test
    void readingUnsupportedTypeThrowsJsonException() {
        assertThatThrownBy(() -> codec.read("{}", String.class)).isInstanceOf(JsonException.class);
    }

    @Test
    void writingUnsupportedTypeThrowsJsonException() {
        assertThatThrownBy(() -> codec.write(42)).isInstanceOf(JsonException.class);
    }
}
