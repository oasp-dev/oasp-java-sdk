package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.ConversationState;
import dev.oasp.client.types.CreateConversation;
import dev.oasp.client.types.ErrorEnvelope;
import dev.oasp.client.types.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Round-trips {@link Conversation}, {@link CreateConversation}, and {@link ErrorEnvelope}. */
class ConversationRoundTripTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

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
}
