package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class PrincipalRefTest {

    @Test
    void constructsWithValidArguments() {
        PrincipalRef ref = new PrincipalRef(PrincipalKind.USER, "user-1");

        assertThat(ref.kind()).isEqualTo(PrincipalKind.USER);
        assertThat(ref.id()).isEqualTo("user-1");
    }

    @Test
    void rejectsNullKind() {
        assertThatNullPointerException()
                .isThrownBy(() -> new PrincipalRef(null, "user-1"))
                .withMessageContaining("kind");
    }

    @Test
    void rejectsNullId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new PrincipalRef(PrincipalKind.USER, null))
                .withMessageContaining("id");
    }
}
