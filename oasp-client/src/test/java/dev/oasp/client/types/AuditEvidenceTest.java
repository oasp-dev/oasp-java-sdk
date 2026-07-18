package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class AuditEvidenceTest {

    @Test
    void constructsWithValidArguments() {
        AgentVersionRef versionRef = new AgentVersionRef("agent-1", 1L);
        AuditEvidence evidence = new AuditEvidence(Optional.of("sha256:abc"), Optional.of(versionRef));

        assertThat(evidence.contentDigest()).contains("sha256:abc");
        assertThat(evidence.agentVersionRef()).contains(versionRef);
    }

    @Test
    void normalizesNullFieldsToEmpty() {
        AuditEvidence evidence = new AuditEvidence(null, null);

        assertThat(evidence.contentDigest()).isEmpty();
        assertThat(evidence.agentVersionRef()).isEmpty();
    }
}
