package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.oasp.client.types.AuditEvent;
import dev.oasp.client.types.AuditInteraction;
import dev.oasp.client.types.AuditOutcome;
import dev.oasp.client.types.AuditRefs;
import dev.oasp.client.types.AuditWho;
import dev.oasp.client.types.Principal;
import dev.oasp.client.types.PrincipalIdentity;
import dev.oasp.client.types.PrincipalKind;
import dev.oasp.client.types.PrincipalRef;
import dev.oasp.client.types.Scope;
import dev.oasp.client.types.ScopeLevel;
import dev.oasp.client.types.SessionRunStatus;
import dev.oasp.client.types.StatusEvent;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Every protocol enum must (de)serialize to the SPEC's wire value, not the
 * Java constant name - several differ in casing (e.g. {@code
 * CREATE_CONVERSATION} <-> {@code "createConversation"}).
 */
class EnumWireValueTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

    @Test
    void principalKindWritesItsWireValueNotItsJavaName() {
        Principal principal = new Principal(
                "principal-1",
                PrincipalKind.USER,
                new PrincipalIdentity("sub-1", Optional.empty(), Optional.empty(), Optional.empty()),
                List.of(new Scope(ScopeLevel.WORKSPACE, "ws-1")),
                List.of());

        String json = codec.write(principal);

        assertThat(json).contains("\"kind\":\"user\"").contains("\"level\":\"workspace\"");
    }

    @Test
    void auditInteractionWritesItsCamelCaseWireValue() {
        AuditEvent event = new AuditEvent(
                "audit-1",
                new AuditWho(new PrincipalRef(PrincipalKind.USER, "user-1"), Optional.empty()),
                AuditInteraction.CREATE_CONVERSATION,
                Optional.empty(),
                Instant.parse("2026-01-01T00:00:00Z"),
                AuditOutcome.NOT_FOUND,
                Optional.empty(),
                new AuditRefs(Optional.empty(), Optional.empty(), Optional.empty(), List.of()),
                Optional.empty());

        String json = codec.write(event);

        assertThat(json).contains("\"what\":\"createConversation\"").contains("\"outcome\":\"not_found\"");
    }

    @Test
    void sessionRunStatusWritesItsWireValue() {
        StatusEvent event = new StatusEvent("evt-1", Instant.parse("2026-01-01T00:00:00Z"), SessionRunStatus.IDLE);

        assertThat(codec.write(event)).contains("\"status\":\"idle\"");
    }

    @Test
    void unrecognisedPrincipalKindThrowsJsonException() {
        String json = "{\"resourceType\":\"Principal\",\"id\":\"principal-1\",\"kind\":\"root\","
                + "\"identity\":{\"subject\":\"sub-1\"},\"scopeMemberships\":[],\"roles\":[]}";

        assertThatThrownBy(() -> codec.read(json, Principal.class)).isInstanceOf(JsonException.class);
    }

    @Test
    void unrecognisedScopeLevelThrowsJsonException() {
        String json = "{\"level\":\"planet\",\"id\":\"x\"}";

        assertThatThrownBy(() -> codec.read(json, Scope.class)).isInstanceOf(JsonException.class);
    }

    @Test
    void unrecognisedAuditOutcomeThrowsJsonException() {
        String json = "{\"resourceType\":\"AuditEvent\",\"id\":\"audit-1\","
                + "\"who\":{\"principal\":{\"kind\":\"user\",\"id\":\"user-1\"}},"
                + "\"what\":\"createConversation\",\"when\":\"2026-01-01T00:00:00Z\","
                + "\"outcome\":\"maybe\",\"refs\":{}}";

        assertThatThrownBy(() -> codec.read(json, AuditEvent.class)).isInstanceOf(JsonException.class);
    }
}
