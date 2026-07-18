package dev.oasp.client;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Shared setup for the transport tests: a per-test WireMock server on a
 * dynamic port and an {@link OaspClient} pointed at it with a fixed bearer
 * token, plus a loader for the standalone JSON/SSE fixtures under {@code
 * src/test/resources/fixtures/}.
 */
abstract class WireMockTestBase {

    static final String TOKEN = "test-token";

    protected WireMockServer server;
    protected OaspClient client;

    @BeforeEach
    void startServer() {
        server = new WireMockServer(options().dynamicPort());
        server.start();
        // Point the static WireMock DSL (stubFor/verify) at this test's server.
        configureFor("localhost", server.port());
        client = OaspClient.builder()
                .baseUrl(URI.create(server.baseUrl()))
                .tokenProvider(() -> TOKEN)
                .connectTimeout(Duration.ofSeconds(2))
                .requestTimeout(Duration.ofSeconds(2))
                .build();
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }

    /** Reads a fixture file from {@code src/test/resources/fixtures/} as UTF-8 text. */
    static String fixture(String name) {
        String path = "/fixtures/" + name;
        try (InputStream in = WireMockTestBase.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Fixture not found on classpath: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed reading fixture: " + path, e);
        }
    }

    /** Assembles an SSE response body from fixture lines, ensuring a trailing blank line terminates the last frame. */
    static String sseBody(String fixtureName) {
        return fixture(fixtureName).lines().collect(Collectors.joining("\n")) + "\n\n";
    }
}
