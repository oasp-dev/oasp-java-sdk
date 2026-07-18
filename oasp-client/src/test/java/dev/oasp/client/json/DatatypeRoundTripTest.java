package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.AgentVersionRef;
import dev.oasp.client.types.PrincipalIdentity;
import dev.oasp.client.types.PrincipalKind;
import dev.oasp.client.types.PrincipalRef;
import dev.oasp.client.types.Scope;
import dev.oasp.client.types.ScopeLevel;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Round-trips the embedded datatypes directly, not just nested inside a resource. */
class DatatypeRoundTripTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

    @Test
    void roundTripsScope() {
        Scope scope = new Scope(ScopeLevel.WORKSPACE, "ws-1");

        assertThat(codec.read(codec.write(scope), Scope.class)).isEqualTo(scope);
    }

    @Test
    void roundTripsPrincipalRef() {
        PrincipalRef ref = new PrincipalRef(PrincipalKind.AGENT, "agent-1");

        assertThat(codec.read(codec.write(ref), PrincipalRef.class)).isEqualTo(ref);
    }

    @Test
    void roundTripsAgentVersionRef() {
        AgentVersionRef ref = new AgentVersionRef("definition-1", 42L);

        assertThat(codec.read(codec.write(ref), AgentVersionRef.class)).isEqualTo(ref);
    }

    @Test
    void roundTripsPrincipalIdentityWithAllOptionalFieldsPresent() {
        PrincipalIdentity identity = new PrincipalIdentity(
                "sub-1", Optional.of("issuer-1"), Optional.of("Ada Lovelace"), Optional.of("ada@example.com"));

        assertThat(codec.read(codec.write(identity), PrincipalIdentity.class)).isEqualTo(identity);
    }

    @Test
    void roundTripsPrincipalIdentityWithAllOptionalFieldsAbsent() {
        PrincipalIdentity identity = new PrincipalIdentity("sub-1", Optional.empty(), Optional.empty(), Optional.empty());

        assertThat(codec.read(codec.write(identity), PrincipalIdentity.class)).isEqualTo(identity);
    }

    @Test
    void absentOptionalFieldsAreOmittedFromTheWrittenJson() {
        PrincipalIdentity identity = new PrincipalIdentity("sub-1", Optional.empty(), Optional.empty(), Optional.empty());

        String json = codec.write(identity);

        assertThat(json).doesNotContain("issuer").doesNotContain("displayName").doesNotContain("email");
    }
}
