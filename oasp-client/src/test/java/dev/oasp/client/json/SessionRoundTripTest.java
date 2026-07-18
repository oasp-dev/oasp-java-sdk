package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.AgentVersionRef;
import dev.oasp.client.types.FileResource;
import dev.oasp.client.types.GithubRepositoryResource;
import dev.oasp.client.types.MemoryStoreResource;
import dev.oasp.client.types.Session;
import dev.oasp.client.types.UnknownSessionResource;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SessionRoundTripTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

    @Test
    void roundTripsWithEveryMountedResourceVariant() {
        Session session = new Session(
                "session-1",
                new AgentVersionRef("agent-1", 1L),
                List.of(
                        new FileResource("file-1"),
                        new MemoryStoreResource("store-1"),
                        new GithubRepositoryResource("octocat", "hello-world", Optional.of("main")),
                        new GithubRepositoryResource("octocat", "hello-world", Optional.empty())),
                List.of("vault-1"));

        Session result = codec.read(codec.write(session), Session.class);

        assertThat(result).isEqualTo(session);
    }

    @Test
    void roundTripsWithNoMountedResources() {
        Session session = new Session("session-2", new AgentVersionRef("agent-1", 1L), List.of(), List.of());

        Session result = codec.read(codec.write(session), Session.class);

        assertThat(result).isEqualTo(session);
    }

    @Test
    void anUnrecognisedMountedResourceTypeMapsToUnknownSessionResource() {
        String json =
                "{\"resourceType\":\"Session\",\"id\":\"session-1\","
                        + "\"pinnedAgentVersion\":{\"agentDefinitionId\":\"agent-1\",\"version\":1},"
                        + "\"resources\":[{\"type\":\"mcp_server\",\"url\":\"https://example.com\"}],"
                        + "\"vaultIds\":[]}";

        Session session = codec.read(json, Session.class);

        assertThat(session.resources()).hasSize(1);
        assertThat(session.resources().get(0)).isInstanceOf(UnknownSessionResource.class);
        UnknownSessionResource unknown = (UnknownSessionResource) session.resources().get(0);
        assertThat(unknown.type()).isEqualTo("mcp_server");
    }
}
