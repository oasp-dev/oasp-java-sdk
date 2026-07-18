package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.AuditEvent;
import dev.oasp.client.types.ConversationClosed;
import dev.oasp.client.types.ConversationCreated;
import dev.oasp.client.types.Principal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Round-trips {@link ConversationCreated}/{@link ConversationClosed} as {@link AuditEvent}. */
class AuditEventRoundTripTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

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
}
