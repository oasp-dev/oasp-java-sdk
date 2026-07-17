package dev.oasp.client.error;

import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.ErrorEnvelope;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProtocolErrorsTest {

    private static final ErrorEnvelope ENVELOPE =
            new ErrorEnvelope("conversation_already_closed", "the conversation is already closed");

    @Test
    void mapsUnauthorizedToAuthExceptionWithStatus() {
        OaspException exception = ProtocolErrors.fromResponse(401, ENVELOPE);

        assertThat(exception).isInstanceOf(OaspAuthException.class);
        assertThat(((OaspAuthException) exception).status()).isEqualTo(401);
        assertThat(exception.getMessage()).isEqualTo(ENVELOPE.message());
    }

    @Test
    void mapsForbiddenToAuthExceptionWithStatus() {
        OaspException exception = ProtocolErrors.fromResponse(403, ENVELOPE);

        assertThat(exception).isInstanceOf(OaspAuthException.class);
        assertThat(((OaspAuthException) exception).status()).isEqualTo(403);
        assertThat(exception.getMessage()).isEqualTo(ENVELOPE.message());
    }

    @Test
    void mapsNotFoundToNotFoundException() {
        OaspException exception = ProtocolErrors.fromResponse(404, ENVELOPE);

        assertThat(exception).isInstanceOf(OaspNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(ENVELOPE.message());
    }

    @Test
    void mapsConflictToConflictException() {
        OaspException exception = ProtocolErrors.fromResponse(409, ENVELOPE);

        assertThat(exception).isInstanceOf(OaspConflictException.class);
        assertThat(exception.getMessage()).isEqualTo(ENVELOPE.message());
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 422, 500, 503})
    void mapsOtherStatusesToProtocolExceptionCarryingStatus(int status) {
        OaspException exception = ProtocolErrors.fromResponse(status, ENVELOPE);

        assertThat(exception).isInstanceOf(OaspProtocolException.class);
        OaspProtocolException protocolException = (OaspProtocolException) exception;
        assertThat(protocolException.status()).isEqualTo(status);
        assertThat(protocolException.envelope()).contains(ENVELOPE);
        assertThat(protocolException.getMessage()).isEqualTo(ENVELOPE.message());
    }

    @Test
    void protocolExceptionEnvelopeReflectsCodeAndMessageWhenPresent() {
        OaspProtocolException exception =
                (OaspProtocolException) ProtocolErrors.fromResponse(500, ENVELOPE);

        assertThat(exception.envelope()).isPresent();
        assertThat(exception.envelope().get().code()).isEqualTo(ENVELOPE.code());
        assertThat(exception.envelope().get().message()).isEqualTo(ENVELOPE.message());
    }

    @Test
    void protocolExceptionEnvelopeIsEmptyWhenMapperGivenNullEnvelope() {
        OaspProtocolException exception = (OaspProtocolException) ProtocolErrors.fromResponse(500, null);

        assertThat(exception.envelope()).isEmpty();
    }

    @Test
    void messageFallsBackToStatusOnlyWhenEnvelopeIsNull() {
        OaspException exception = ProtocolErrors.fromResponse(404, null);

        assertThat(exception.getMessage()).contains("404");
    }

    @Test
    void authExceptionMessageFallsBackWhenEnvelopeIsNull() {
        OaspException exception = ProtocolErrors.fromResponse(401, null);

        assertThat(exception.getMessage()).contains("401");
    }
}
