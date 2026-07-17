package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConversationCreatedTest {

    private static final Principal ACTOR = new Principal("user-1", List.of());
    private static final Instant OCCURRED_AT = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void constructsWithValidArguments() {
        ConversationCreated event = new ConversationCreated("conv-1", OCCURRED_AT, ACTOR);

        assertThat(event.conversationId()).isEqualTo("conv-1");
        assertThat(event.occurredAt()).isEqualTo(OCCURRED_AT);
        assertThat(event.actor()).isEqualTo(ACTOR);
    }

    @Test
    void rejectsNullConversationId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new ConversationCreated(null, OCCURRED_AT, ACTOR))
                .withMessageContaining("conversationId");
    }

    @Test
    void rejectsNullOccurredAt() {
        assertThatNullPointerException()
                .isThrownBy(() -> new ConversationCreated("conv-1", null, ACTOR))
                .withMessageContaining("occurredAt");
    }

    @Test
    void rejectsNullActor() {
        assertThatNullPointerException()
                .isThrownBy(() -> new ConversationCreated("conv-1", OCCURRED_AT, null))
                .withMessageContaining("actor");
    }
}
