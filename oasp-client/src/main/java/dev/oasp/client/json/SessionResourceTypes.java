package dev.oasp.client.json;

/**
 * The {@code type} discriminator wire values for {@link
 * dev.oasp.client.types.SessionResource} variants, per oasp-standard's
 * {@code session.ts}. Collected in one place so {@link
 * SessionResourceWriters} and {@link SessionResourceReaders} share exactly
 * the same strings.
 */
final class SessionResourceTypes {

    static final String FILE = "file";
    static final String MEMORY_STORE = "memory_store";
    static final String GITHUB_REPOSITORY = "github_repository";

    private SessionResourceTypes() {}
}
