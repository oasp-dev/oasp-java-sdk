package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AuditRefsTest {

    @Test
    void constructsWithValidArguments() {
        AuditRefs refs = new AuditRefs(
                Optional.of("session-1"), Optional.of("conv-1"), Optional.of("agent-1"), List.of("cred-1"));

        assertThat(refs.sessionId()).contains("session-1");
        assertThat(refs.conversationId()).contains("conv-1");
        assertThat(refs.definitionId()).contains("agent-1");
        assertThat(refs.credentialIds()).containsExactly("cred-1");
    }

    @Test
    void normalizesAllAbsentFieldsToEmpty() {
        AuditRefs refs = new AuditRefs(null, null, null, null);

        assertThat(refs.sessionId()).isEmpty();
        assertThat(refs.conversationId()).isEmpty();
        assertThat(refs.definitionId()).isEmpty();
        assertThat(refs.credentialIds()).isEmpty();
    }
}
