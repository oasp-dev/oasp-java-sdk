package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class ScopeClaimTest {

    @Test
    void constructsWithValidArguments() {
        ScopeClaim claim = new ScopeClaim(ScopeLevel.TENANT, "tenant-1");

        assertThat(claim.level()).isEqualTo(ScopeLevel.TENANT);
        assertThat(claim.id()).isEqualTo("tenant-1");
    }

    @Test
    void rejectsNullLevel() {
        assertThatNullPointerException()
                .isThrownBy(() -> new ScopeClaim(null, "tenant-1"))
                .withMessageContaining("level");
    }

    @Test
    void rejectsNullId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new ScopeClaim(ScopeLevel.TENANT, null))
                .withMessageContaining("id");
    }

    @Test
    void rejectsBlankId() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new ScopeClaim(ScopeLevel.TENANT, "   "))
                .withMessageContaining("id");
    }
}
