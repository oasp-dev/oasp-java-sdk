package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PrincipalTest {

    private static final PrincipalIdentity IDENTITY =
            new PrincipalIdentity("sub-1", Optional.empty(), Optional.empty(), Optional.empty());

    @Test
    void constructsWithValidArguments() {
        Principal principal = new Principal(
                "principal-1",
                PrincipalKind.USER,
                IDENTITY,
                List.of(new Scope(ScopeLevel.WORKSPACE, "ws-1")),
                List.of("admin"));

        assertThat(principal.id()).isEqualTo("principal-1");
        assertThat(principal.kind()).isEqualTo(PrincipalKind.USER);
        assertThat(principal.identity()).isEqualTo(IDENTITY);
        assertThat(principal.scopeMemberships()).containsExactly(new Scope(ScopeLevel.WORKSPACE, "ws-1"));
        assertThat(principal.roles()).containsExactly("admin");
    }

    @Test
    void resourceTypeIsPrincipal() {
        Principal principal = new Principal("principal-1", PrincipalKind.USER, IDENTITY, List.of(), List.of());

        assertThat(principal.resourceType()).isEqualTo("Principal");
    }

    @Test
    void defensivelyCopiesScopeMembershipsAndRoles() {
        List<Scope> scopes = new ArrayList<>();
        scopes.add(new Scope(ScopeLevel.WORKSPACE, "ws-1"));
        List<String> roles = new ArrayList<>();
        roles.add("admin");

        Principal principal = new Principal("principal-1", PrincipalKind.USER, IDENTITY, scopes, roles);
        scopes.add(new Scope(ScopeLevel.TENANT, "tenant-1"));
        roles.add("owner");

        assertThat(principal.scopeMemberships()).hasSize(1);
        assertThat(principal.roles()).hasSize(1);
    }

    @Test
    void rejectsNullId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Principal(null, PrincipalKind.USER, IDENTITY, List.of(), List.of()))
                .withMessageContaining("id");
    }

    @Test
    void rejectsNullKind() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Principal("principal-1", null, IDENTITY, List.of(), List.of()))
                .withMessageContaining("kind");
    }

    @Test
    void rejectsNullIdentity() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Principal("principal-1", PrincipalKind.USER, null, List.of(), List.of()))
                .withMessageContaining("identity");
    }

    @Test
    void rejectsNullScopeMemberships() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Principal("principal-1", PrincipalKind.USER, IDENTITY, null, List.of()))
                .withMessageContaining("scopeMemberships");
    }

    @Test
    void rejectsNullRoles() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Principal("principal-1", PrincipalKind.USER, IDENTITY, List.of(), null))
                .withMessageContaining("roles");
    }
}
