package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.AgentVersionRef;
import dev.oasp.client.types.AssistantMessageStartEvent;
import dev.oasp.client.types.AuditEvent;
import dev.oasp.client.types.AuditInteraction;
import dev.oasp.client.types.AuditOutcome;
import dev.oasp.client.types.AuditRefs;
import dev.oasp.client.types.AuditWho;
import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.Event;
import dev.oasp.client.types.Principal;
import dev.oasp.client.types.PrincipalIdentity;
import dev.oasp.client.types.PrincipalKind;
import dev.oasp.client.types.PrincipalRef;
import dev.oasp.client.types.Resource;
import dev.oasp.client.types.Scope;
import dev.oasp.client.types.ScopeLevel;
import dev.oasp.client.types.Session;
import dev.oasp.client.types.UnknownResource;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * {@code read(json, Resource.class)}: the {@code resourceType} discriminator
 * dispatch that is the core new mechanism this mapping layer adds over the
 * v0 realignment - and its forward-compatible fallback for a {@code
 * resourceType} this SDK version has never heard of.
 */
class ResourceDispatchTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

    @Test
    void dispatchesPrincipalByResourceType() {
        Principal principal = new Principal(
                "principal-1",
                PrincipalKind.USER,
                new PrincipalIdentity("sub-1", Optional.empty(), Optional.empty(), Optional.empty()),
                List.of(),
                List.of());

        Resource result = codec.read(codec.write(principal), Resource.class);

        assertThat(result).isInstanceOf(Principal.class).isEqualTo(principal);
    }

    @Test
    void dispatchesConversationByResourceType() {
        Conversation conversation = new Conversation(
                "conv-1",
                new Scope(ScopeLevel.WORKSPACE, "ws-1"),
                new PrincipalRef(PrincipalKind.USER, "user-1"),
                "session-1",
                new AgentVersionRef("agent-1", 1L),
                List.of());

        Resource result = codec.read(codec.write(conversation), Resource.class);

        assertThat(result).isInstanceOf(Conversation.class).isEqualTo(conversation);
    }

    @Test
    void dispatchesSessionByResourceType() {
        Session session = new Session("session-1", new AgentVersionRef("agent-1", 1L), List.of(), List.of());

        Resource result = codec.read(codec.write(session), Resource.class);

        assertThat(result).isInstanceOf(Session.class).isEqualTo(session);
    }

    @Test
    void dispatchesAuditEventByResourceType() {
        AuditEvent event = new AuditEvent(
                "audit-1",
                new AuditWho(new PrincipalRef(PrincipalKind.USER, "user-1"), Optional.empty()),
                AuditInteraction.CREATE_CONVERSATION,
                Optional.empty(),
                Instant.parse("2026-01-01T00:00:00Z"),
                AuditOutcome.SUCCESS,
                Optional.empty(),
                new AuditRefs(Optional.empty(), Optional.empty(), Optional.empty(), List.of()),
                Optional.empty());

        Resource result = codec.read(codec.write(event), Resource.class);

        assertThat(result).isInstanceOf(AuditEvent.class).isEqualTo(event);
    }

    @Test
    void dispatchesEventByResourceType() {
        Event event = new AssistantMessageStartEvent("evt-1", Instant.parse("2026-01-01T00:00:00Z"), "msg-1");

        Resource result = codec.read(codec.write(event), Resource.class);

        assertThat(result).isInstanceOf(Event.class).isEqualTo(event);
    }

    @Test
    void unrecognisedResourceTypeMapsToUnknownResourceNotAnException() {
        String json = "{\"resourceType\":\"Deployment\",\"id\":\"deploy-1\",\"status\":\"active\"}";

        Resource result = codec.read(json, Resource.class);

        assertThat(result).isInstanceOf(UnknownResource.class);
        UnknownResource unknown = (UnknownResource) result;
        assertThat(unknown.resourceType()).isEqualTo("Deployment");
        assertThat(unknown.rawJson()).isEqualTo(json);
    }

    @Test
    void writingAnUnknownResourceEmitsRawJsonVerbatim() {
        String rawJson = "{\"resourceType\":\"Deployment\",\"id\":\"deploy-1\",\"extra\":\"field\"}";
        UnknownResource unknown = new UnknownResource("Deployment", rawJson);

        // Byte-for-byte, not just structurally equal - even the unmapped
        // "extra" field must survive.
        assertThat(codec.write(unknown)).isEqualTo(rawJson);
    }
}
