package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AuditEventTest {

    private static final AuditWho WHO = new AuditWho(new PrincipalRef(PrincipalKind.USER, "user-1"), Optional.empty());
    private static final Instant WHEN = Instant.parse("2026-01-01T00:00:00Z");
    private static final AuditRefs REFS = new AuditRefs(Optional.empty(), Optional.of("conv-1"), Optional.empty(), List.of());

    @Test
    void constructsWithValidArguments() {
        AuditEvent event = new AuditEvent(
                "audit-1",
                WHO,
                AuditInteraction.CREATE_CONVERSATION,
                Optional.of(new Scope(ScopeLevel.WORKSPACE, "ws-1")),
                WHEN,
                AuditOutcome.SUCCESS,
                Optional.empty(),
                REFS,
                Optional.empty());

        assertThat(event.id()).isEqualTo("audit-1");
        assertThat(event.who()).isEqualTo(WHO);
        assertThat(event.what()).isEqualTo(AuditInteraction.CREATE_CONVERSATION);
        assertThat(event.when()).isEqualTo(WHEN);
        assertThat(event.outcome()).isEqualTo(AuditOutcome.SUCCESS);
        assertThat(event.refs()).isEqualTo(REFS);
    }

    @Test
    void resourceTypeIsAuditEvent() {
        AuditEvent event = new AuditEvent(
                "audit-1",
                WHO,
                AuditInteraction.STREAM,
                Optional.empty(),
                WHEN,
                AuditOutcome.NOT_FOUND,
                Optional.empty(),
                REFS,
                Optional.empty());

        assertThat(event.resourceType()).isEqualTo("AuditEvent");
    }

    @Test
    void allowsAbsentScopeEvenWhenOutcomeIsNotNotFound() {
        // Deliberately lenient: the spec's cross-field rule (scope required
        // unless outcome is not_found) is not enforced here - the server is
        // the source of truth, same convention as Conversation.closedAt.
        AuditEvent event = new AuditEvent(
                "audit-1",
                WHO,
                AuditInteraction.PUBLISH,
                Optional.empty(),
                WHEN,
                AuditOutcome.SUCCESS,
                Optional.empty(),
                REFS,
                Optional.empty());

        assertThat(event.scope()).isEmpty();
    }

    @Test
    void normalizesNullOptionalsToEmpty() {
        AuditEvent event = new AuditEvent(
                "audit-1", WHO, AuditInteraction.DRAIN, null, WHEN, AuditOutcome.FAILURE, null, REFS, null);

        assertThat(event.scope()).isEmpty();
        assertThat(event.degraded()).isEmpty();
        assertThat(event.evidence()).isEmpty();
    }

    @Test
    void rejectsNullId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new AuditEvent(
                        null, WHO, AuditInteraction.SEND, Optional.empty(), WHEN, AuditOutcome.SUCCESS,
                        Optional.empty(), REFS, Optional.empty()))
                .withMessageContaining("id");
    }

    @Test
    void rejectsNullWho() {
        assertThatNullPointerException()
                .isThrownBy(() -> new AuditEvent(
                        "audit-1", null, AuditInteraction.SEND, Optional.empty(), WHEN, AuditOutcome.SUCCESS,
                        Optional.empty(), REFS, Optional.empty()))
                .withMessageContaining("who");
    }

    @Test
    void rejectsNullWhat() {
        assertThatNullPointerException()
                .isThrownBy(() -> new AuditEvent(
                        "audit-1", WHO, null, Optional.empty(), WHEN, AuditOutcome.SUCCESS,
                        Optional.empty(), REFS, Optional.empty()))
                .withMessageContaining("what");
    }

    @Test
    void rejectsNullWhen() {
        assertThatNullPointerException()
                .isThrownBy(() -> new AuditEvent(
                        "audit-1", WHO, AuditInteraction.SEND, Optional.empty(), null, AuditOutcome.SUCCESS,
                        Optional.empty(), REFS, Optional.empty()))
                .withMessageContaining("when");
    }

    @Test
    void rejectsNullOutcome() {
        assertThatNullPointerException()
                .isThrownBy(() -> new AuditEvent(
                        "audit-1", WHO, AuditInteraction.SEND, Optional.empty(), WHEN, null,
                        Optional.empty(), REFS, Optional.empty()))
                .withMessageContaining("outcome");
    }

    @Test
    void rejectsNullRefs() {
        assertThatNullPointerException()
                .isThrownBy(() -> new AuditEvent(
                        "audit-1", WHO, AuditInteraction.SEND, Optional.empty(), WHEN, AuditOutcome.SUCCESS,
                        Optional.empty(), null, Optional.empty()))
                .withMessageContaining("refs");
    }
}
