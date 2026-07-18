package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.AgentVersionRef;
import dev.oasp.client.types.AuditEvent;
import dev.oasp.client.types.AuditEvidence;
import dev.oasp.client.types.AuditInteraction;
import dev.oasp.client.types.AuditOutcome;
import dev.oasp.client.types.AuditRefs;
import dev.oasp.client.types.AuditWho;
import dev.oasp.client.types.PrincipalKind;
import dev.oasp.client.types.PrincipalRef;
import dev.oasp.client.types.Scope;
import dev.oasp.client.types.ScopeLevel;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AuditEventRoundTripTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

    @Test
    void roundTripsWithEveryOptionalFieldPresent() {
        AuditEvent event = new AuditEvent(
                "audit-1",
                new AuditWho(
                        new PrincipalRef(PrincipalKind.USER, "user-1"),
                        Optional.of(new PrincipalRef(PrincipalKind.AGENT, "agent-1"))),
                AuditInteraction.SEND,
                Optional.of(new Scope(ScopeLevel.WORKSPACE, "ws-1")),
                Instant.parse("2026-01-01T00:00:00Z"),
                AuditOutcome.SUCCESS,
                Optional.of(true),
                new AuditRefs(
                        Optional.of("session-1"), Optional.of("conv-1"), Optional.of("def-1"), List.of("cred-1")),
                Optional.of(new AuditEvidence(
                        Optional.of("sha256:abcdef"), Optional.of(new AgentVersionRef("agent-1", 1L)))));

        AuditEvent result = codec.read(codec.write(event), AuditEvent.class);

        assertThat(result).isEqualTo(event);
    }

    @Test
    void roundTripsWithEveryOptionalFieldAbsent() {
        AuditEvent event = new AuditEvent(
                "audit-2",
                new AuditWho(new PrincipalRef(PrincipalKind.USER, "user-1"), Optional.empty()),
                AuditInteraction.CREATE_CONVERSATION,
                Optional.empty(),
                Instant.parse("2026-01-01T00:00:00Z"),
                AuditOutcome.NOT_FOUND,
                Optional.empty(),
                new AuditRefs(Optional.empty(), Optional.empty(), Optional.empty(), List.of()),
                Optional.empty());

        AuditEvent result = codec.read(codec.write(event), AuditEvent.class);

        assertThat(result).isEqualTo(event);
    }

    @Test
    void absentOptionalFieldsAreOmittedFromTheWrittenJson() {
        AuditEvent event = new AuditEvent(
                "audit-2",
                new AuditWho(new PrincipalRef(PrincipalKind.USER, "user-1"), Optional.empty()),
                AuditInteraction.CREATE_CONVERSATION,
                Optional.empty(),
                Instant.parse("2026-01-01T00:00:00Z"),
                AuditOutcome.NOT_FOUND,
                Optional.empty(),
                new AuditRefs(Optional.empty(), Optional.empty(), Optional.empty(), List.of()),
                Optional.empty());

        String json = codec.write(event);

        assertThat(json)
                .doesNotContain("\"scope\"")
                .doesNotContain("\"degraded\"")
                .doesNotContain("\"evidence\"")
                .doesNotContain("\"onBehalfOf\"");
    }
}
