package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class ScopeTest {

    @Test
    void constructsWithValidArguments() {
        Scope scope = new Scope(ScopeLevel.WORKSPACE, "ws-1");

        assertThat(scope.level()).isEqualTo(ScopeLevel.WORKSPACE);
        assertThat(scope.id()).isEqualTo("ws-1");
    }

    @Test
    void rejectsNullLevel() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Scope(null, "ws-1"))
                .withMessageContaining("level");
    }

    @Test
    void rejectsNullId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Scope(ScopeLevel.WORKSPACE, null))
                .withMessageContaining("id");
    }

    @Test
    void allowsBlankId() {
        // Deliberately lenient: Scope is inbound data embedded in multiple
        // resources, so - per this package's convention - only non-null is
        // checked, never blank.
        Scope scope = new Scope(ScopeLevel.WORKSPACE, "");

        assertThat(scope.id()).isEmpty();
    }
}
