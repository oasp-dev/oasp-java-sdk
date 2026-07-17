package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class ErrorEnvelopeTest {

    @Test
    void constructsWithValidArguments() {
        ErrorEnvelope envelope = new ErrorEnvelope("conversation_already_closed", "already closed");

        assertThat(envelope.code()).isEqualTo("conversation_already_closed");
        assertThat(envelope.message()).isEqualTo("already closed");
    }

    @Test
    void rejectsNullCode() {
        assertThatNullPointerException()
                .isThrownBy(() -> new ErrorEnvelope(null, "already closed"))
                .withMessageContaining("code");
    }

    @Test
    void rejectsNullMessage() {
        assertThatNullPointerException()
                .isThrownBy(() -> new ErrorEnvelope("conversation_already_closed", null))
                .withMessageContaining("message");
    }

    @Test
    void allowsBlankCodeAndMessage() {
        // Deliberately lenient (unlike, say, ScopeClaim.id): this is inbound
        // server data, and a "blank" code/message from the server is still
        // decodable information we shouldn't reject.
        ErrorEnvelope envelope = new ErrorEnvelope("", "");

        assertThat(envelope.code()).isEmpty();
        assertThat(envelope.message()).isEmpty();
    }
}
