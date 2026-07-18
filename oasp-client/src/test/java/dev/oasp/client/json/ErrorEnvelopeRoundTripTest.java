package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.ErrorEnvelope;
import org.junit.jupiter.api.Test;

class ErrorEnvelopeRoundTripTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

    @Test
    void roundTrips() {
        ErrorEnvelope envelope =
                new ErrorEnvelope("conversation_already_closed", "The conversation is already closed.");

        ErrorEnvelope result = codec.read(codec.write(envelope), ErrorEnvelope.class);

        assertThat(result).isEqualTo(envelope);
    }
}
