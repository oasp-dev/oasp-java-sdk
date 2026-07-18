package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SessionTest {

    private static final AgentVersionRef PINNED_VERSION = new AgentVersionRef("agent-1", 1L);

    @Test
    void constructsWithValidArguments() {
        Session session = new Session(
                "session-1", PINNED_VERSION, List.of(new FileResource("file-1")), List.of("vault-1"));

        assertThat(session.id()).isEqualTo("session-1");
        assertThat(session.pinnedAgentVersion()).isEqualTo(PINNED_VERSION);
        assertThat(session.resources()).containsExactly(new FileResource("file-1"));
        assertThat(session.vaultIds()).containsExactly("vault-1");
    }

    @Test
    void resourceTypeIsSession() {
        Session session = new Session("session-1", PINNED_VERSION, List.of(), List.of());

        assertThat(session.resourceType()).isEqualTo("Session");
    }

    @Test
    void defensivelyCopiesResourcesAndVaultIds() {
        List<SessionResource> resources = new ArrayList<>();
        resources.add(new FileResource("file-1"));
        List<String> vaultIds = new ArrayList<>();
        vaultIds.add("vault-1");

        Session session = new Session("session-1", PINNED_VERSION, resources, vaultIds);
        resources.add(new MemoryStoreResource("store-1"));
        vaultIds.add("vault-2");

        assertThat(session.resources()).hasSize(1);
        assertThat(session.vaultIds()).hasSize(1);
    }

    @Test
    void rejectsNullId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Session(null, PINNED_VERSION, List.of(), List.of()))
                .withMessageContaining("id");
    }

    @Test
    void rejectsNullPinnedAgentVersion() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Session("session-1", null, List.of(), List.of()))
                .withMessageContaining("pinnedAgentVersion");
    }

    @Test
    void rejectsNullResources() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Session("session-1", PINNED_VERSION, null, List.of()))
                .withMessageContaining("resources");
    }

    @Test
    void rejectsNullVaultIds() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Session("session-1", PINNED_VERSION, List.of(), null))
                .withMessageContaining("vaultIds");
    }
}
