package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class GithubRepositoryResourceTest {

    @Test
    void constructsWithValidArguments() {
        GithubRepositoryResource resource =
                new GithubRepositoryResource("oasp-dev", "oasp-standard", Optional.of("main"));

        assertThat(resource.owner()).isEqualTo("oasp-dev");
        assertThat(resource.repo()).isEqualTo("oasp-standard");
        assertThat(resource.ref()).contains("main");
        assertThat(resource.type()).isEqualTo("github_repository");
    }

    @Test
    void normalizesNullRefToEmpty() {
        GithubRepositoryResource resource = new GithubRepositoryResource("oasp-dev", "oasp-standard", null);

        assertThat(resource.ref()).isEmpty();
    }

    @Test
    void rejectsNullOwner() {
        assertThatNullPointerException()
                .isThrownBy(() -> new GithubRepositoryResource(null, "oasp-standard", Optional.empty()))
                .withMessageContaining("owner");
    }

    @Test
    void rejectsNullRepo() {
        assertThatNullPointerException()
                .isThrownBy(() -> new GithubRepositoryResource("oasp-dev", null, Optional.empty()))
                .withMessageContaining("repo");
    }
}
