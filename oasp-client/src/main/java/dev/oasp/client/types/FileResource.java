package dev.oasp.client.types;

import java.util.Objects;

/**
 * A single file mounted into a {@link Session} at create.
 *
 * @param fileId identifier of the file mounted into the session
 */
public record FileResource(String fileId) implements SessionResource {

    public FileResource {
        Objects.requireNonNull(fileId, "fileId");
    }

    @Override
    public String type() {
        return "file";
    }
}
