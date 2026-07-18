package dev.oasp.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.Session;
import org.junit.jupiter.api.Test;

class SessionsTest extends WireMockTestBase {

    @Test
    void sendPostsTheMessageAndAcceptsA202WithNoBody() {
        stubFor(post(urlEqualTo("/sessions/session-1/messages")).willReturn(aResponse().withStatus(202)));

        client.sessions().send("session-1", "weave me a thread");

        verify(postRequestedFor(urlEqualTo("/sessions/session-1/messages"))
                .withRequestBody(equalToJson("{\"content\":\"weave me a thread\"}")));
    }

    @Test
    void drainReturnsTheSession() {
        stubFor(post(urlEqualTo("/sessions/session-1/drain"))
                .willReturn(aResponse().withStatus(200).withBody(fixture("session.json"))));

        Session session = client.sessions().drain("session-1");

        assertThat(session.id()).isEqualTo("session-1");
        assertThat(session.pinnedAgentVersion().version()).isEqualTo(1L);
    }
}
