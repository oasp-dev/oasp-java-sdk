package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class UnknownResourceTest {

    @Test
    void constructsWithValidArguments() {
        UnknownResource resource = new UnknownResource("Deployment", "{\"resourceType\":\"Deployment\"}");

        assertThat(resource.resourceType()).isEqualTo("Deployment");
        assertThat(resource.rawJson()).isEqualTo("{\"resourceType\":\"Deployment\"}");
    }

    @Test
    void rejectsNullResourceType() {
        assertThatNullPointerException()
                .isThrownBy(() -> new UnknownResource(null, "{}"))
                .withMessageContaining("resourceType");
    }

    @Test
    void rejectsNullRawJson() {
        assertThatNullPointerException()
                .isThrownBy(() -> new UnknownResource("Deployment", null))
                .withMessageContaining("rawJson");
    }
}
