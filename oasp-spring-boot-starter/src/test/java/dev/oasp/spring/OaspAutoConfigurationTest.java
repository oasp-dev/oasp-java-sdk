package dev.oasp.spring;

import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.OaspClient;
import dev.oasp.client.http.TokenProvider;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class OaspAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(OaspAutoConfiguration.class));

    @Test
    void registersClientWhenBaseUrlSet() {
        runner.withPropertyValues("oasp.base-url=https://api.example.com", "oasp.token=t")
                .run(context -> assertThat(context).hasSingleBean(OaspClient.class));
    }

    @Test
    void bindsPropertiesIncludingDurations() {
        runner.withPropertyValues(
                        "oasp.base-url=https://api.example.com",
                        "oasp.token=t",
                        "oasp.connect-timeout=5s",
                        "oasp.request-timeout=30s")
                .run(context -> {
                    OaspProperties props = context.getBean(OaspProperties.class);
                    assertThat(props.baseUrl()).hasToString("https://api.example.com");
                    assertThat(props.token()).isEqualTo("t");
                    assertThat(props.connectTimeout()).isEqualTo(Duration.ofSeconds(5));
                    assertThat(props.requestTimeout()).isEqualTo(Duration.ofSeconds(30));
                });
    }

    @Test
    void timeoutsDefaultWhenUnset() {
        runner.withPropertyValues("oasp.base-url=https://api.example.com", "oasp.token=t")
                .run(context -> {
                    OaspProperties props = context.getBean(OaspProperties.class);
                    assertThat(props.connectTimeout()).isEqualTo(Duration.ofSeconds(5));
                    assertThat(props.requestTimeout()).isEqualTo(Duration.ofSeconds(30));
                });
    }

    @Test
    void consumerClientBeanOverridesAutoConfigured() {
        OaspClient custom = OaspClient.builder()
                .baseUrl(java.net.URI.create("https://custom.example.com"))
                .tokenProvider(() -> "custom")
                .build();
        runner.withPropertyValues("oasp.base-url=https://api.example.com", "oasp.token=t")
                .withBean(OaspClient.class, () -> custom)
                .run(context -> assertThat(context.getBean(OaspClient.class)).isSameAs(custom));
    }

    @Test
    void tokenProviderBeanWinsOverStaticToken() {
        runner.withPropertyValues("oasp.base-url=https://api.example.com", "oasp.token=static")
                .withUserConfiguration(TokenProviderConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(OaspClient.class);
                    assertThat(context.getBean(TokenProvider.class).getToken()).isEqualTo("from-bean");
                });
    }

    @Test
    void missingBaseUrlFailsClearly() {
        runner.withPropertyValues("oasp.token=t")
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void missingTokenAndProviderFailsClearly() {
        runner.withPropertyValues("oasp.base-url=https://api.example.com")
                .run(context -> assertThat(context)
                        .getFailure()
                        .rootCause()
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("oasp.token"));
    }

    @Configuration
    static class TokenProviderConfig {
        @Bean
        TokenProvider tokenProvider() {
            return () -> "from-bean";
        }
    }
}
