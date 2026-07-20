package dev.oasp.sample.streaming;

import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.OaspClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Confirms the whole app context comes up: the starter auto-configured an
 * {@link OaspClient} bean and the MVC / SSE layers wired up cleanly.
 *
 * <p>The properties give the client a base URL and a dummy token so it builds
 * successfully. Building an OaspClient validates config but does not open a
 * connection, so no OASP server is needed for this test.
 */
@SpringBootTest
@TestPropertySource(properties = {"oasp.base-url=https://loom.local:8443", "oasp.token=test-token"})
class StreamingSampleApplicationTests {

    @Autowired
    private OaspClient oaspClient;

    @Test
    void contextLoads() {
        assertThat(oaspClient).isNotNull();
    }
}
