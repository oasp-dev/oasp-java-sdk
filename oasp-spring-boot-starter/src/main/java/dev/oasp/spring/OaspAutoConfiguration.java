package dev.oasp.spring;

import dev.oasp.client.OaspClient;
import dev.oasp.client.http.TokenProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configures an {@link OaspClient} from {@code oasp.*} properties. A
 * consumer can supply their own {@link OaspClient} bean to opt out entirely.
 */
@AutoConfiguration
@EnableConfigurationProperties(OaspProperties.class)
public class OaspAutoConfiguration {

    private final OaspProperties properties;

    OaspAutoConfiguration(OaspProperties properties) {
        this.properties = properties;
    }

    /**
     * Builds the client from configuration. A {@link TokenProvider} bean in the
     * context wins over the static {@code oasp.token}; if neither is present the
     * build fails clearly.
     */
    @Bean
    @ConditionalOnMissingBean(OaspClient.class)
    OaspClient oaspClient(ObjectProvider<TokenProvider> tokenProviders) {
        return OaspClient.builder()
                .baseUrl(properties.baseUrl())
                .tokenProvider(resolveTokenProvider(tokenProviders))
                .connectTimeout(properties.connectTimeout())
                .requestTimeout(properties.requestTimeout())
                .build();
    }

    private TokenProvider resolveTokenProvider(ObjectProvider<TokenProvider> tokenProviders) {
        TokenProvider bean = tokenProviders.getIfAvailable();
        if (bean != null) {
            return bean;
        }
        String token = properties.token();
        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "No OASP token: define a TokenProvider bean or set the 'oasp.token' property.");
        }
        return () -> token;
    }
}
