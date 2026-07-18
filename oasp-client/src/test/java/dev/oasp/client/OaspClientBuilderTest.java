package dev.oasp.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import org.junit.jupiter.api.Test;

/** Builder validation: the two required inputs must be present before {@code build()}. */
class OaspClientBuilderTest {

    @Test
    void rejectsMissingBaseUrl() {
        var builder = OaspClient.builder().tokenProvider(() -> "t");

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("baseUrl");
    }

    @Test
    void rejectsMissingTokenProvider() {
        var builder = OaspClient.builder().baseUrl(URI.create("https://api.example.com"));

        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("tokenProvider");
    }

    @Test
    void buildsWhenRequiredInputsPresent() {
        var client = OaspClient.builder()
                .baseUrl(URI.create("https://api.example.com"))
                .tokenProvider(() -> "t")
                .build();

        assertThat(client.conversations()).isNotNull();
        assertThat(client.sessions()).isNotNull();
    }
}
