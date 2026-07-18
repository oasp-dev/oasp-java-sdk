package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class AgentVersionRefTest {

    @Test
    void constructsWithValidArguments() {
        AgentVersionRef ref = new AgentVersionRef("agent-1", 3L);

        assertThat(ref.agentDefinitionId()).isEqualTo("agent-1");
        assertThat(ref.version()).isEqualTo(3L);
    }

    @Test
    void rejectsNullAgentDefinitionId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new AgentVersionRef(null, 3L))
                .withMessageContaining("agentDefinitionId");
    }
}
