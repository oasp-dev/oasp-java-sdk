package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class UnknownAuditEventTest {

    private static final Principal ACTOR = new Principal("user-1", List.of());
    private static final Instant OCCURRED_AT = Instant.parse("2026-01-01T00:00:00Z");
    private static final String TYPE = "conversation.archived";
    private static final String RAW_JSON = "{\"type\":\"conversation.archived\"}";

    @Test
    void constructsWithValidArguments() {
        UnknownAuditEvent event =
                new UnknownAuditEvent("conv-1", OCCURRED_AT, ACTOR, TYPE, RAW_JSON);

        assertThat(event.conversationId()).isEqualTo("conv-1");
        assertThat(event.occurredAt()).isEqualTo(OCCURRED_AT);
        assertThat(event.actor()).isEqualTo(ACTOR);
        assertThat(event.type()).isEqualTo(TYPE);
        assertThat(event.rawJson()).isEqualTo(RAW_JSON);
    }

    @Test
    void roundTripsTypeAndRawJson() {
        UnknownAuditEvent event =
                new UnknownAuditEvent("conv-1", OCCURRED_AT, ACTOR, TYPE, RAW_JSON);

        assertThat(event.type()).isEqualTo(TYPE);
        assertThat(event.rawJson()).isEqualTo(RAW_JSON);
    }

    @Test
    void rejectsNullConversationId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new UnknownAuditEvent(null, OCCURRED_AT, ACTOR, TYPE, RAW_JSON))
                .withMessageContaining("conversationId");
    }

    @Test
    void rejectsNullOccurredAt() {
        assertThatNullPointerException()
                .isThrownBy(() -> new UnknownAuditEvent("conv-1", null, ACTOR, TYPE, RAW_JSON))
                .withMessageContaining("occurredAt");
    }

    @Test
    void rejectsNullActor() {
        assertThatNullPointerException()
                .isThrownBy(() -> new UnknownAuditEvent("conv-1", OCCURRED_AT, null, TYPE, RAW_JSON))
                .withMessageContaining("actor");
    }

    @Test
    void rejectsNullType() {
        assertThatNullPointerException()
                .isThrownBy(() -> new UnknownAuditEvent("conv-1", OCCURRED_AT, ACTOR, null, RAW_JSON))
                .withMessageContaining("type");
    }

    @Test
    void rejectsBlankType() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new UnknownAuditEvent("conv-1", OCCURRED_AT, ACTOR, "   ", RAW_JSON))
                .withMessageContaining("type");
    }

    @Test
    void rejectsNullRawJson() {
        assertThatNullPointerException()
                .isThrownBy(() -> new UnknownAuditEvent("conv-1", OCCURRED_AT, ACTOR, TYPE, null))
                .withMessageContaining("rawJson");
    }
}
