package dev.oasp.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.CreateConversation;
import dev.oasp.client.types.PrincipalKind;
import dev.oasp.client.types.PrincipalRef;
import dev.oasp.client.types.Scope;
import dev.oasp.client.types.ScopeLevel;
import java.util.List;
import org.junit.jupiter.api.Test;

class CreateConversationTest extends WireMockTestBase {

    private static final CreateConversation REQUEST = new CreateConversation(
            new Scope(ScopeLevel.WORKSPACE, "ws-1"),
            new PrincipalRef(PrincipalKind.USER, "user-1"),
            "agent-1",
            List.of());

    @Test
    void createReturnsConversationFromResponseBody() {
        stubFor(post(urlEqualTo("/conversations"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(fixture("conversation.json"))));

        Conversation conversation = client.conversations().create(REQUEST);

        assertThat(conversation.id()).isEqualTo("conv-1");
        assertThat(conversation.currentSessionId()).isEqualTo("session-1");
        assertThat(conversation.pinnedAgentVersion().agentDefinitionId()).isEqualTo("agent-1");
    }

    @Test
    void createSendsBearerTokenOnTheRequest() {
        stubFor(post(urlEqualTo("/conversations"))
                .willReturn(aResponse().withStatus(201).withBody(fixture("conversation.json"))));

        client.conversations().create(REQUEST);

        verify(postRequestedFor(urlEqualTo("/conversations"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN)));
    }
}
