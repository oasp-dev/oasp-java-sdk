package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.oasp.client.types.AuditEvent;
import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.ConversationClosed;
import dev.oasp.client.types.ConversationCreated;
import dev.oasp.client.types.ConversationState;
import dev.oasp.client.types.CreateConversation;
import dev.oasp.client.types.ErrorEnvelope;
import dev.oasp.client.types.Principal;
import dev.oasp.client.types.ScopeClaim;
import dev.oasp.client.types.ScopeLevel;
import dev.oasp.client.types.UnknownAuditEvent;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Round-trips every protocol type through {@link HandRolledJsonCodec}
 * (write, then read the result back) and checks the result equals the
 * original - plus the deliberately-not-a-round-trip cases: the
 * unknown-{@code AuditEvent} fallback and malformed input.
 */
class HandRolledJsonCodecTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

    // ------------------------------------------------------------------
    // Round trips
    // ------------------------------------------------------------------

    @Test
    void roundTripsPrincipalWithClaims() {
        Principal principal =
                new Principal(
                        "user-1",
                        List.of(
                                new ScopeClaim(ScopeLevel.TENANT, "tenant-1"),
                                new ScopeClaim(ScopeLevel.ROLE, "admin")));

        Principal result = codec.read(codec.write(principal), Principal.class);

        assertThat(result).isEqualTo(principal);
    }

    @Test
    void roundTripsPrincipalWithNoClaims() {
        Principal principal = new Principal("user-1", List.of());

        Principal result = codec.read(codec.write(principal), Principal.class);

        assertThat(result).isEqualTo(principal);
        assertThat(result.claims()).isEmpty();
    }

    @Test
    void roundTripsScopeClaim() {
        ScopeClaim claim = new ScopeClaim(ScopeLevel.WORKSPACE, "workspace-1");

        ScopeClaim result = codec.read(codec.write(claim), ScopeClaim.class);

        assertThat(result).isEqualTo(claim);
    }

    @Test
    void roundTripsEveryScopeLevel() {
        for (ScopeLevel level : ScopeLevel.values()) {
            ScopeLevel result = codec.read(codec.write(level), ScopeLevel.class);
            assertThat(result).isEqualTo(level);
        }
    }

    @Test
    void roundTripsEveryConversationState() {
        for (ConversationState state : ConversationState.values()) {
            ConversationState result = codec.read(codec.write(state), ConversationState.class);
            assertThat(result).isEqualTo(state);
        }
    }

    @Test
    void roundTripsConversationWithClosedAtPresent() {
        Principal principal = new Principal("user-1", List.of());
        Conversation conversation =
                new Conversation(
                        "conv-1",
                        ConversationState.CLOSED,
                        principal,
                        Instant.parse("2026-01-01T00:00:00Z"),
                        Optional.of(Instant.parse("2026-01-02T00:00:00Z")));

        Conversation result = codec.read(codec.write(conversation), Conversation.class);

        assertThat(result).isEqualTo(conversation);
        assertThat(result.closedAt()).contains(Instant.parse("2026-01-02T00:00:00Z"));
    }

    @Test
    void roundTripsConversationWithClosedAtEmpty() {
        Principal principal = new Principal("user-1", List.of());
        Conversation conversation =
                new Conversation(
                        "conv-1",
                        ConversationState.OPEN,
                        principal,
                        Instant.parse("2026-01-01T00:00:00Z"),
                        Optional.empty());

        String json = codec.write(conversation);
        Conversation result = codec.read(json, Conversation.class);

        assertThat(result).isEqualTo(conversation);
        assertThat(result.closedAt()).isEmpty();
        // An empty closedAt must serialize as JSON null, not be omitted or
        // rendered some other way.
        assertThat(json).contains("\"closedAt\":null");
    }

    @Test
    void roundTripsCreateConversation() {
        CreateConversation createConversation =
                CreateConversation.forPrincipal(new Principal("user-1", List.of()));

        CreateConversation result = codec.read(codec.write(createConversation), CreateConversation.class);

        assertThat(result).isEqualTo(createConversation);
    }

    @Test
    void roundTripsErrorEnvelope() {
        ErrorEnvelope envelope = new ErrorEnvelope("conversation_already_closed", "The conversation is closed.");

        ErrorEnvelope result = codec.read(codec.write(envelope), ErrorEnvelope.class);

        assertThat(result).isEqualTo(envelope);
    }

    @Test
    void roundTripsConversationCreatedAsAuditEvent() {
        ConversationCreated created =
                new ConversationCreated(
                        "conv-1", Instant.parse("2026-01-01T00:00:00Z"), new Principal("user-1", List.of()));

        AuditEvent result = codec.read(codec.write(created), AuditEvent.class);

        assertThat(result).isEqualTo(created).isInstanceOf(ConversationCreated.class);
    }

    @Test
    void roundTripsConversationClosedAsAuditEvent() {
        ConversationClosed closed =
                new ConversationClosed(
                        "conv-1", Instant.parse("2026-01-01T00:00:00Z"), new Principal("user-1", List.of()));

        AuditEvent result = codec.read(codec.write(closed), AuditEvent.class);

        assertThat(result).isEqualTo(closed).isInstanceOf(ConversationClosed.class);
    }

    @Test
    void writtenConversationCreatedCarriesItsDiscriminator() {
        ConversationCreated created =
                new ConversationCreated(
                        "conv-1", Instant.parse("2026-01-01T00:00:00Z"), new Principal("user-1", List.of()));

        assertThat(codec.write(created)).contains("\"type\":\"conversation.created\"");
    }

    @Test
    void writtenConversationClosedCarriesItsDiscriminator() {
        ConversationClosed closed =
                new ConversationClosed(
                        "conv-1", Instant.parse("2026-01-01T00:00:00Z"), new Principal("user-1", List.of()));

        assertThat(codec.write(closed)).contains("\"type\":\"conversation.closed\"");
    }

    // ------------------------------------------------------------------
    // Unknown AuditEvent fallback
    // ------------------------------------------------------------------

    @Test
    void unrecognisedAuditEventTypeMapsToUnknownAuditEventNotAnException() {
        String json =
                "{\"conversationId\":\"conv-1\",\"occurredAt\":\"2026-01-01T00:00:00Z\","
                        + "\"actor\":{\"subject\":\"user-1\",\"claims\":[]},"
                        + "\"type\":\"conversation.archived\"}";

        AuditEvent event = codec.read(json, AuditEvent.class);

        assertThat(event).isInstanceOf(UnknownAuditEvent.class);
        UnknownAuditEvent unknown = (UnknownAuditEvent) event;
        assertThat(unknown.type()).isEqualTo("conversation.archived");
        assertThat(unknown.rawJson()).isEqualTo(json);
        assertThat(unknown.conversationId()).isEqualTo("conv-1");
        assertThat(unknown.occurredAt()).isEqualTo(Instant.parse("2026-01-01T00:00:00Z"));
        assertThat(unknown.actor()).isEqualTo(new Principal("user-1", List.of()));
    }

    @Test
    void writingAnUnknownAuditEventEmitsRawJsonVerbatim() {
        String rawJson =
                "{\"conversationId\":\"conv-1\",\"occurredAt\":\"2026-01-01T00:00:00Z\","
                        + "\"actor\":{\"subject\":\"user-1\",\"claims\":[]},"
                        + "\"type\":\"conversation.archived\",\"extra\":\"field\"}";
        UnknownAuditEvent unknown =
                new UnknownAuditEvent(
                        "conv-1",
                        Instant.parse("2026-01-01T00:00:00Z"),
                        new Principal("user-1", List.of()),
                        "conversation.archived",
                        rawJson);

        // Byte-for-byte, not just structurally equal - even the unmapped
        // "extra" field must survive.
        assertThat(codec.write(unknown)).isEqualTo(rawJson);
    }

    // ------------------------------------------------------------------
    // Enums are strict on read
    // ------------------------------------------------------------------

    @Test
    void unrecognisedScopeLevelThrowsJsonException() {
        assertThatThrownBy(() -> codec.read("\"NOT_A_LEVEL\"", ScopeLevel.class))
                .isInstanceOf(JsonException.class);
    }

    @Test
    void unrecognisedConversationStateThrowsJsonException() {
        assertThatThrownBy(() -> codec.read("\"NOT_A_STATE\"", ConversationState.class))
                .isInstanceOf(JsonException.class);
    }

    // ------------------------------------------------------------------
    // Malformed input at the codec level
    // ------------------------------------------------------------------

    @Test
    void readingMalformedJsonThrowsJsonException() {
        assertThatThrownBy(() -> codec.read("{\"subject\":", Principal.class))
                .isInstanceOf(JsonException.class);
    }

    @Test
    void readingWrongShapeThrowsJsonException() {
        // A JSON array where an object is required.
        assertThatThrownBy(() -> codec.read("[1,2,3]", Principal.class))
                .isInstanceOf(JsonException.class);
    }

    @Test
    void readingUnsupportedTypeThrowsJsonException() {
        assertThatThrownBy(() -> codec.read("{}", String.class)).isInstanceOf(JsonException.class);
    }
}
