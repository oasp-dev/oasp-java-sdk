package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.List;
import org.junit.jupiter.api.Test;

class CreateConversationTest {

    private static final Scope SCOPE = new Scope(ScopeLevel.WORKSPACE, "ws-1");
    private static final PrincipalRef INITIATING_PRINCIPAL = new PrincipalRef(PrincipalKind.USER, "user-1");

    @Test
    void constructsWithValidArguments() {
        CreateConversation request =
                new CreateConversation(SCOPE, INITIATING_PRINCIPAL, "agent-1", List.of(new FileResource("file-1")));

        assertThat(request.scope()).isEqualTo(SCOPE);
        assertThat(request.initiatingPrincipal()).isEqualTo(INITIATING_PRINCIPAL);
        assertThat(request.definitionId()).isEqualTo("agent-1");
        assertThat(request.resources()).containsExactly(new FileResource("file-1"));
    }

    @Test
    void rejectsNullScope() {
        assertThatNullPointerException()
                .isThrownBy(() -> new CreateConversation(null, INITIATING_PRINCIPAL, "agent-1", List.of()))
                .withMessageContaining("scope");
    }

    @Test
    void rejectsNullInitiatingPrincipal() {
        assertThatNullPointerException()
                .isThrownBy(() -> new CreateConversation(SCOPE, null, "agent-1", List.of()))
                .withMessageContaining("initiatingPrincipal");
    }

    @Test
    void rejectsNullDefinitionId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new CreateConversation(SCOPE, INITIATING_PRINCIPAL, null, List.of()))
                .withMessageContaining("definitionId");
    }

    @Test
    void rejectsBlankDefinitionId() {
        // Unlike the inbound resource types elsewhere in this package, this
        // is an outbound request we construct ourselves, so its own
        // identifier gets a blank check too.
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new CreateConversation(SCOPE, INITIATING_PRINCIPAL, "  ", List.of()))
                .withMessageContaining("definitionId");
    }

    @Test
    void rejectsNullResources() {
        assertThatNullPointerException()
                .isThrownBy(() -> new CreateConversation(SCOPE, INITIATING_PRINCIPAL, "agent-1", null))
                .withMessageContaining("resources");
    }
}
