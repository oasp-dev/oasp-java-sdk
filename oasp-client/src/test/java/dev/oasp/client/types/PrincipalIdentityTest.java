package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class PrincipalIdentityTest {

    @Test
    void constructsWithValidArguments() {
        PrincipalIdentity identity = new PrincipalIdentity(
                "sub-1", Optional.of("issuer-1"), Optional.of("Ada"), Optional.of("ada@example.com"));

        assertThat(identity.subject()).isEqualTo("sub-1");
        assertThat(identity.issuer()).contains("issuer-1");
        assertThat(identity.displayName()).contains("Ada");
        assertThat(identity.email()).contains("ada@example.com");
    }

    @Test
    void normalizesNullOptionalsToEmpty() {
        PrincipalIdentity identity = new PrincipalIdentity("sub-1", null, null, null);

        assertThat(identity.issuer()).isEmpty();
        assertThat(identity.displayName()).isEmpty();
        assertThat(identity.email()).isEmpty();
    }

    @Test
    void rejectsNullSubject() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new PrincipalIdentity(null, Optional.empty(), Optional.empty(), Optional.empty()))
                .withMessageContaining("subject");
    }
}
