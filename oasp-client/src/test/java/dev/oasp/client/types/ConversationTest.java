package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.List;
import org.junit.jupiter.api.Test;

class ConversationTest {

    private static final Scope SCOPE = new Scope(ScopeLevel.WORKSPACE, "ws-1");
    private static final PrincipalRef INITIATING_PRINCIPAL = new PrincipalRef(PrincipalKind.USER, "user-1");
    private static final AgentVersionRef PINNED_VERSION = new AgentVersionRef("agent-1", 1L);

    @Test
    void constructsWithValidArguments() {
        Conversation conversation = new Conversation(
                "conv-1", SCOPE, INITIATING_PRINCIPAL, "session-1", PINNED_VERSION, List.of("session-0"));

        assertThat(conversation.id()).isEqualTo("conv-1");
        assertThat(conversation.scope()).isEqualTo(SCOPE);
        assertThat(conversation.initiatingPrincipal()).isEqualTo(INITIATING_PRINCIPAL);
        assertThat(conversation.currentSessionId()).isEqualTo("session-1");
        assertThat(conversation.pinnedAgentVersion()).isEqualTo(PINNED_VERSION);
        assertThat(conversation.previousSessionIds()).containsExactly("session-0");
    }

    @Test
    void resourceTypeIsConversation() {
        Conversation conversation =
                new Conversation("conv-1", SCOPE, INITIATING_PRINCIPAL, "session-1", PINNED_VERSION, List.of());

        assertThat(conversation.resourceType()).isEqualTo("Conversation");
    }

    @Test
    void rejectsNullId() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new Conversation(null, SCOPE, INITIATING_PRINCIPAL, "session-1", PINNED_VERSION, List.of()))
                .withMessageContaining("id");
    }

    @Test
    void rejectsNullScope() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new Conversation("conv-1", null, INITIATING_PRINCIPAL, "session-1", PINNED_VERSION, List.of()))
                .withMessageContaining("scope");
    }

    @Test
    void rejectsNullInitiatingPrincipal() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Conversation("conv-1", SCOPE, null, "session-1", PINNED_VERSION, List.of()))
                .withMessageContaining("initiatingPrincipal");
    }

    @Test
    void rejectsNullCurrentSessionId() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new Conversation("conv-1", SCOPE, INITIATING_PRINCIPAL, null, PINNED_VERSION, List.of()))
                .withMessageContaining("currentSessionId");
    }

    @Test
    void rejectsNullPinnedAgentVersion() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new Conversation("conv-1", SCOPE, INITIATING_PRINCIPAL, "session-1", null, List.of()))
                .withMessageContaining("pinnedAgentVersion");
    }

    @Test
    void rejectsNullPreviousSessionIds() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Conversation(
                        "conv-1", SCOPE, INITIATING_PRINCIPAL, "session-1", PINNED_VERSION, null))
                .withMessageContaining("previousSessionIds");
    }
}
