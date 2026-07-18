package dev.oasp.spring;

import java.net.URI;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Binds the {@code oasp.*} configuration for the auto-configured client.
 *
 * @param baseUrl        required OASP server root, e.g. {@code https://api.example.com}
 * @param token          static bearer token (v0); ignored when a {@code TokenProvider} bean exists
 * @param connectTimeout TCP connect timeout, defaults to 5s
 * @param requestTimeout response timeout, defaults to 30s
 */
@ConfigurationProperties(prefix = "oasp")
public record OaspProperties(
        URI baseUrl,
        String token,
        @DefaultValue("5s") Duration connectTimeout,
        @DefaultValue("30s") Duration requestTimeout) {
}
