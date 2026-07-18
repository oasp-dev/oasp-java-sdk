package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.ConversationState;
import dev.oasp.client.types.Principal;
import dev.oasp.client.types.ScopeClaim;
import dev.oasp.client.types.ScopeLevel;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Round-trips {@link Principal}, {@link ScopeClaim}, and the two enums through {@link HandRolledJsonCodec}. */
class ScalarRoundTripTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

    @Test
    void roundTripsPrincipalWithClaims() {
        Principal principal =
                new Principal(
                        "user-1",
                        List.of(
                                new ScopeClaim(ScopeLevel.TENANT, "tenant-1"),
                                new ScopeClaim(ScopeLevel.ROLE, "admin")));

        Principal result = codec.read(codec.write(principal), Principal.class);

        assertThat(result).isEqualTo(principal);
    }

    @Test
    void roundTripsPrincipalWithNoClaims() {
        Principal principal = new Principal("user-1", List.of());

        Principal result = codec.read(codec.write(principal), Principal.class);

        assertThat(result).isEqualTo(principal);
        assertThat(result.claims()).isEmpty();
    }

    @Test
    void roundTripsScopeClaim() {
        ScopeClaim claim = new ScopeClaim(ScopeLevel.WORKSPACE, "workspace-1");

        ScopeClaim result = codec.read(codec.write(claim), ScopeClaim.class);

        assertThat(result).isEqualTo(claim);
    }

    @Test
    void roundTripsEveryScopeLevel() {
        for (ScopeLevel level : ScopeLevel.values()) {
            ScopeLevel result = codec.read(codec.write(level), ScopeLevel.class);
            assertThat(result).isEqualTo(level);
        }
    }

    @Test
    void roundTripsEveryConversationState() {
        for (ConversationState state : ConversationState.values()) {
            ConversationState result = codec.read(codec.write(state), ConversationState.class);
            assertThat(result).isEqualTo(state);
        }
    }
}
