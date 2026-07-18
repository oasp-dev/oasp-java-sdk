package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class UnknownSessionResourceTest {

    @Test
    void constructsWithValidArguments() {
        UnknownSessionResource resource = new UnknownSessionResource("mcp_server", "{\"type\":\"mcp_server\"}");

        assertThat(resource.type()).isEqualTo("mcp_server");
        assertThat(resource.rawJson()).isEqualTo("{\"type\":\"mcp_server\"}");
    }

    @Test
    void rejectsNullType() {
        assertThatNullPointerException()
                .isThrownBy(() -> new UnknownSessionResource(null, "{}"))
                .withMessageContaining("type");
    }

    @Test
    void rejectsNullRawJson() {
        assertThatNullPointerException()
                .isThrownBy(() -> new UnknownSessionResource("mcp_server", null))
                .withMessageContaining("rawJson");
    }
}
