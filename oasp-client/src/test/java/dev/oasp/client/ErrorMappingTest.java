package dev.oasp.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import dev.oasp.client.error.OaspAuthException;
import dev.oasp.client.error.OaspException;
import dev.oasp.client.error.OaspProtocolException;
import dev.oasp.client.types.CreateConversation;
import dev.oasp.client.types.PrincipalKind;
import dev.oasp.client.types.PrincipalRef;
import dev.oasp.client.types.Scope;
import dev.oasp.client.types.ScopeLevel;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Verifies each HTTP error status maps to the matching {@link OaspException} subclass. */
class ErrorMappingTest extends WireMockTestBase {

    private static final CreateConversation REQUEST = new CreateConversation(
            new Scope(ScopeLevel.WORKSPACE, "ws-1"),
            new PrincipalRef(PrincipalKind.USER, "user-1"),
            "agent-1",
            List.of());

    @ParameterizedTest
    @CsvSource({
        "401, dev.oasp.client.error.OaspAuthException",
        "403, dev.oasp.client.error.OaspAuthException",
        "404, dev.oasp.client.error.OaspNotFoundException",
        "409, dev.oasp.client.error.OaspConflictException",
        "400, dev.oasp.client.error.OaspProtocolException",
        "500, dev.oasp.client.error.OaspProtocolException",
    })
    void mapsStatusToException(int status, Class<? extends OaspException> expected) {
        stubError(status);

        assertThatThrownBy(() -> client.conversations().create(REQUEST)).isInstanceOf(expected);
    }

    @Test
    void protocolExceptionKeepsStatusAndEnvelope() {
        stubError(500);

        var thrown = catchThrowableOfType(OaspProtocolException.class, () -> client.conversations()
                .create(REQUEST));

        assertThat(thrown.status()).isEqualTo(500);
        assertThat(thrown.envelope().orElseThrow().code()).isEqualTo("conversation_already_closed");
    }

    @Test
    void authExceptionUsesTheEnvelopeMessage() {
        stubError(401);

        assertThatThrownBy(() -> client.conversations().create(REQUEST))
                .isInstanceOf(OaspAuthException.class)
                .hasMessageContaining("already been closed");
    }

    private void stubError(int status) {
        stubFor(post(urlEqualTo("/conversations"))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody(fixture("error-envelope.json"))));
    }
}
