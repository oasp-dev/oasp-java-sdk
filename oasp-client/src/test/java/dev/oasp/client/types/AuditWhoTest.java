package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class AuditWhoTest {

    private static final PrincipalRef PRINCIPAL = new PrincipalRef(PrincipalKind.AGENT, "assistant-1");
    private static final PrincipalRef ON_BEHALF_OF = new PrincipalRef(PrincipalKind.USER, "user-1");

    @Test
    void constructsWithValidArguments() {
        AuditWho who = new AuditWho(PRINCIPAL, Optional.of(ON_BEHALF_OF));

        assertThat(who.principal()).isEqualTo(PRINCIPAL);
        assertThat(who.onBehalfOf()).contains(ON_BEHALF_OF);
    }

    @Test
    void normalizesNullOnBehalfOfToEmpty() {
        AuditWho who = new AuditWho(PRINCIPAL, null);

        assertThat(who.onBehalfOf()).isEmpty();
    }

    @Test
    void rejectsNullPrincipal() {
        assertThatNullPointerException()
                .isThrownBy(() -> new AuditWho(null, Optional.empty()))
                .withMessageContaining("principal");
    }
}
