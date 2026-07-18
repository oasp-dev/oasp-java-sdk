package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Sanity-checks that every permitted {@link Resource} implementation
 * reports the {@code resourceType()} oasp-standard's {@code resourceType}
 * discriminator requires, dispatched polymorphically through the sealed
 * interface rather than the concrete type.
 */
class ResourceTest {

    @Test
    void everyPermittedResourceReportsItsOwnResourceType() {
        Resource principal = new Principal(
                "principal-1",
                PrincipalKind.USER,
                new PrincipalIdentity("sub-1", Optional.empty(), Optional.empty(), Optional.empty()),
                List.of(),
                List.of());
        Resource conversation = new Conversation(
                "conv-1",
                new Scope(ScopeLevel.WORKSPACE, "ws-1"),
                new PrincipalRef(PrincipalKind.USER, "user-1"),
                "session-1",
                new AgentVersionRef("agent-1", 1L),
                List.of());
        Resource session = new Session("session-1", new AgentVersionRef("agent-1", 1L), List.of(), List.of());
        Resource unknown = new UnknownResource("Deployment", "{}");

        assertThat(principal.resourceType()).isEqualTo("Principal");
        assertThat(conversation.resourceType()).isEqualTo("Conversation");
        assertThat(session.resourceType()).isEqualTo("Session");
        assertThat(unknown.resourceType()).isEqualTo("Deployment");
    }
}
