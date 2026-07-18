package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class MemoryStoreResourceTest {

    @Test
    void constructsWithValidArguments() {
        MemoryStoreResource resource = new MemoryStoreResource("store-1");

        assertThat(resource.storeId()).isEqualTo("store-1");
        assertThat(resource.type()).isEqualTo("memory_store");
    }

    @Test
    void rejectsNullStoreId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new MemoryStoreResource(null))
                .withMessageContaining("storeId");
    }
}
