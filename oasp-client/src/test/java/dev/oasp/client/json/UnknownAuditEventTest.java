package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.AuditEvent;
import dev.oasp.client.types.Principal;
import dev.oasp.client.types.UnknownAuditEvent;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

/** The unrecognised-{@code AuditEvent} fallback: mapping in, and byte-for-byte write back out. */
class UnknownAuditEventTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

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
}
