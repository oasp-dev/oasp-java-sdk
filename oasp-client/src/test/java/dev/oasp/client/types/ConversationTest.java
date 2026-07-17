package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ConversationTest {

    private static final Principal PRINCIPAL = new Principal("user-1", List.of());
    private static final Instant CREATED_AT = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void constructsWithValidArguments() {
        Conversation conversation = new Conversation(
                "conv-1", ConversationState.OPEN, PRINCIPAL, CREATED_AT, Optional.empty());

        assertThat(conversation.id()).isEqualTo("conv-1");
        assertThat(conversation.state()).isEqualTo(ConversationState.OPEN);
        assertThat(conversation.principal()).isEqualTo(PRINCIPAL);
        assertThat(conversation.createdAt()).isEqualTo(CREATED_AT);
    }

    @Test
    void closedAtIsEmptyWhenConstructedWithNull() {
        Conversation conversation =
                new Conversation("conv-1", ConversationState.OPEN, PRINCIPAL, CREATED_AT, null);

        assertThat(conversation.closedAt()).isEmpty();
    }

    @Test
    void closedAtIsEmptyWhenConstructedWithOptionalEmpty() {
        Conversation conversation = new Conversation(
                "conv-1", ConversationState.OPEN, PRINCIPAL, CREATED_AT, Optional.empty());

        assertThat(conversation.closedAt()).isEmpty();
    }

    @Test
    void closedAtIsPresentWhenGivenAnInstant() {
        Instant closedAt = Instant.parse("2026-01-02T00:00:00Z");

        Conversation conversation = new Conversation(
                "conv-1", ConversationState.CLOSED, PRINCIPAL, CREATED_AT, Optional.of(closedAt));

        assertThat(conversation.closedAt()).contains(closedAt);
    }

    @Test
    void doesNotRequireClosedAtWhenStateIsClosed() {
        // Deliberately lenient: the server is the source of truth for whether
        // a CLOSED conversation must carry a closedAt timestamp, so this must
        // not throw.
        Conversation conversation =
                new Conversation("conv-1", ConversationState.CLOSED, PRINCIPAL, CREATED_AT, null);

        assertThat(conversation.state()).isEqualTo(ConversationState.CLOSED);
        assertThat(conversation.closedAt()).isEmpty();
    }

    @Test
    void rejectsNullId() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new Conversation(null, ConversationState.OPEN, PRINCIPAL, CREATED_AT, Optional.empty()))
                .withMessageContaining("id");
    }

    @Test
    void rejectsNullState() {
        assertThatNullPointerException()
                .isThrownBy(
                        () -> new Conversation("conv-1", null, PRINCIPAL, CREATED_AT, Optional.empty()))
                .withMessageContaining("state");
    }

    @Test
    void rejectsNullPrincipal() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new Conversation("conv-1", ConversationState.OPEN, null, CREATED_AT, Optional.empty()))
                .withMessageContaining("principal");
    }

    @Test
    void rejectsNullCreatedAt() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new Conversation("conv-1", ConversationState.OPEN, PRINCIPAL, null, Optional.empty()))
                .withMessageContaining("createdAt");
    }
}
