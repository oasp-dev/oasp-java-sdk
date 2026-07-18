package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class FileResourceTest {

    @Test
    void constructsWithValidArguments() {
        FileResource resource = new FileResource("file-1");

        assertThat(resource.fileId()).isEqualTo("file-1");
        assertThat(resource.type()).isEqualTo("file");
    }

    @Test
    void rejectsNullFileId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new FileResource(null))
                .withMessageContaining("fileId");
    }
}
