package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.Principal;
import dev.oasp.client.types.PrincipalIdentity;
import dev.oasp.client.types.PrincipalKind;
import dev.oasp.client.types.Scope;
import dev.oasp.client.types.ScopeLevel;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PrincipalRoundTripTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

    @Test
    void roundTripsWithIdentityFieldsPresent() {
        Principal principal = new Principal(
                "principal-1",
                PrincipalKind.USER,
                new PrincipalIdentity(
                        "sub-1", Optional.of("issuer-1"), Optional.of("Ada Lovelace"), Optional.of("ada@example.com")),
                List.of(new Scope(ScopeLevel.WORKSPACE, "ws-1"), new Scope(ScopeLevel.TENANT, "tenant-1")),
                List.of("admin", "billing"));

        Principal result = codec.read(codec.write(principal), Principal.class);

        assertThat(result).isEqualTo(principal);
    }

    @Test
    void roundTripsWithIdentityFieldsAbsent() {
        Principal principal = new Principal(
                "principal-2",
                PrincipalKind.AGENT,
                new PrincipalIdentity("sub-2", Optional.empty(), Optional.empty(), Optional.empty()),
                List.of(),
                List.of());

        Principal result = codec.read(codec.write(principal), Principal.class);

        assertThat(result).isEqualTo(principal);
    }
}
