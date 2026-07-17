package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PrincipalTest {

    @Test
    void constructsWithValidArguments() {
        List<ScopeClaim> claims = List.of(new ScopeClaim(ScopeLevel.USER, "user-1"));

        Principal principal = new Principal("user-1", claims);

        assertThat(principal.subject()).isEqualTo("user-1");
        assertThat(principal.claims()).containsExactly(new ScopeClaim(ScopeLevel.USER, "user-1"));
    }

    @Test
    void constructsWithEmptyClaims() {
        Principal principal = new Principal("user-1", List.of());

        assertThat(principal.claims()).isEmpty();
    }

    @Test
    void rejectsNullSubject() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Principal(null, List.of()))
                .withMessageContaining("subject");
    }

    @Test
    void rejectsBlankSubject() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Principal("   ", List.of()))
                .withMessageContaining("subject");
    }

    @Test
    void rejectsNullClaims() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Principal("user-1", null))
                .withMessageContaining("claims");
    }

    @Test
    void rejectsNullElementWithinClaims() {
        List<ScopeClaim> claimsWithNull = new ArrayList<>();
        claimsWithNull.add(null);

        assertThatNullPointerException().isThrownBy(() -> new Principal("user-1", claimsWithNull));
    }

    @Test
    void defensivelyCopiesTheSuppliedList() {
        List<ScopeClaim> source = new ArrayList<>();
        source.add(new ScopeClaim(ScopeLevel.USER, "user-1"));

        Principal principal = new Principal("user-1", source);

        // Mutating the caller's original list after construction must not be
        // visible through the Principal - it should hold its own snapshot.
        source.add(new ScopeClaim(ScopeLevel.TENANT, "tenant-1"));

        assertThat(principal.claims()).containsExactly(new ScopeClaim(ScopeLevel.USER, "user-1"));
    }

    @Test
    void storedClaimsListIsUnmodifiable() {
        Principal principal = new Principal("user-1", List.of(new ScopeClaim(ScopeLevel.USER, "user-1")));

        assertThat(principal.claims()).isUnmodifiable();
    }
}
