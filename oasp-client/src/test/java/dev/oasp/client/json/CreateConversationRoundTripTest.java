package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.CreateConversation;
import dev.oasp.client.types.FileResource;
import dev.oasp.client.types.PrincipalKind;
import dev.oasp.client.types.PrincipalRef;
import dev.oasp.client.types.Scope;
import dev.oasp.client.types.ScopeLevel;
import java.util.List;
import org.junit.jupiter.api.Test;

class CreateConversationRoundTripTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

    @Test
    void roundTripsWithMountedResources() {
        CreateConversation request = new CreateConversation(
                new Scope(ScopeLevel.WORKSPACE, "ws-1"),
                new PrincipalRef(PrincipalKind.USER, "user-1"),
                "definition-1",
                List.of(new FileResource("file-1")));

        CreateConversation result = codec.read(codec.write(request), CreateConversation.class);

        assertThat(result).isEqualTo(request);
    }

    @Test
    void roundTripsWithNoMountedResources() {
        CreateConversation request = new CreateConversation(
                new Scope(ScopeLevel.TENANT, "tenant-1"),
                new PrincipalRef(PrincipalKind.SERVICE, "service-1"),
                "definition-1",
                List.of());

        CreateConversation result = codec.read(codec.write(request), CreateConversation.class);

        assertThat(result).isEqualTo(request);
    }
}
