package dev.oasp.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import dev.oasp.client.error.OaspProtocolException;
import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.CreateConversation;
import dev.oasp.client.types.PrincipalKind;
import dev.oasp.client.types.PrincipalRef;
import dev.oasp.client.types.Scope;
import dev.oasp.client.types.ScopeLevel;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * The transport's retry rule: exactly one retry on a connect-level failure
 * (no answer), and zero retries on any status the server actually answered
 * with - a protocol error is an answer, not an outage.
 */
class RetryTest extends WireMockTestBase {

    private static final CreateConversation REQUEST = new CreateConversation(
            new Scope(ScopeLevel.WORKSPACE, "ws-1"),
            new PrincipalRef(PrincipalKind.USER, "user-1"),
            "agent-1",
            List.of());

    @Test
    void retriesConnectFailureExactlyOnceThenSucceeds() {
        String scenario = "connect-retry";
        stubFor(post(urlEqualTo("/conversations"))
                .inScenario(scenario)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER))
                .willSetStateTo("recovered"));
        stubFor(post(urlEqualTo("/conversations"))
                .inScenario(scenario)
                .whenScenarioStateIs("recovered")
                .willReturn(aResponse().withStatus(201).withBody(fixture("conversation.json"))));

        Conversation conversation = client.conversations().create(REQUEST);

        assertThat(conversation.id()).isEqualTo("conv-1");
        verify(2, postRequestedFor(urlEqualTo("/conversations")));
    }

    @Test
    void doesNotRetryA500() {
        stubFor(post(urlEqualTo("/conversations")).willReturn(aResponse().withStatus(500)));

        assertThatThrownBy(() -> client.conversations().create(REQUEST)).isInstanceOf(OaspProtocolException.class);

        verify(1, postRequestedFor(urlEqualTo("/conversations")));
    }
}
