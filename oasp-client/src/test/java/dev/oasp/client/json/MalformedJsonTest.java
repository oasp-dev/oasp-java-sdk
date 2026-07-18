package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.oasp.client.types.ConversationState;
import dev.oasp.client.types.Principal;
import dev.oasp.client.types.ScopeLevel;
import org.junit.jupiter.api.Test;

/** Strict enums on read, plus malformed/wrong-shape/unsupported-type input at the codec level. */
class MalformedJsonTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

    @Test
    void unrecognisedScopeLevelThrowsJsonException() {
        assertThatThrownBy(() -> codec.read("\"NOT_A_LEVEL\"", ScopeLevel.class))
                .isInstanceOf(JsonException.class);
    }

    @Test
    void unrecognisedConversationStateThrowsJsonException() {
        assertThatThrownBy(() -> codec.read("\"NOT_A_STATE\"", ConversationState.class))
                .isInstanceOf(JsonException.class);
    }

    @Test
    void readingMalformedJsonThrowsJsonException() {
        assertThatThrownBy(() -> codec.read("{\"subject\":", Principal.class))
                .isInstanceOf(JsonException.class);
    }

    @Test
    void readingWrongShapeThrowsJsonException() {
        // A JSON array where an object is required.
        assertThatThrownBy(() -> codec.read("[1,2,3]", Principal.class))
                .isInstanceOf(JsonException.class);
    }

    @Test
    void readingUnsupportedTypeThrowsJsonException() {
        assertThatThrownBy(() -> codec.read("{}", String.class)).isInstanceOf(JsonException.class);
    }
}
