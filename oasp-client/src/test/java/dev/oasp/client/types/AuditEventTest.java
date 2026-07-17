package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class AuditEventTest {

    private static final Principal ACTOR = new Principal("user-1", List.of());
    private static final Instant OCCURRED_AT = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void permittedSubclassesAreExactlyTheClosedSet() {
        // AuditEvent.class.getPermittedSubclasses() only returns anything
        // because the interface is `sealed` - this is the reflective proof
        // that the taxonomy really is closed to exactly these three types,
        // not just closed "by convention".
        assertThat(AuditEvent.class.getPermittedSubclasses())
                .containsExactlyInAnyOrder(
                        ConversationCreated.class, ConversationClosed.class, UnknownAuditEvent.class);
    }

    @Test
    void switchOverEveryPermittedTypeReturnsExpectedValue() {
        ConversationCreated created = new ConversationCreated("conv-1", OCCURRED_AT, ACTOR);
        ConversationClosed closed = new ConversationClosed("conv-1", OCCURRED_AT, ACTOR);
        UnknownAuditEvent unknown =
                new UnknownAuditEvent("conv-1", OCCURRED_AT, ACTOR, "conversation.archived", "{}");

        assertThat(describe(created)).isEqualTo("created");
        assertThat(describe(closed)).isEqualTo("closed");
        assertThat(describe(unknown)).isEqualTo("unknown:conversation.archived");
    }

    /**
     * A {@code switch} with one case per permitted type of {@link AuditEvent}
     * and deliberately NO {@code default} branch. This compiles only because
     * {@code AuditEvent} is sealed with exactly these three permitted types:
     * the compiler can see every possible subtype and proves the switch is
     * exhaustive on its own. Remove a case (or add a fourth implementation of
     * {@code AuditEvent} elsewhere without updating this switch) and the
     * build stops compiling instead of failing at runtime - that compile-time
     * guarantee is the entire point of sealing the interface.
     */
    private static String describe(AuditEvent event) {
        return switch (event) {
            case ConversationCreated created -> "created";
            case ConversationClosed closed -> "closed";
            case UnknownAuditEvent unknown -> "unknown:" + unknown.type();
        };
    }
}
