package dev.oasp.client.types;

/**
 * The session-mountable resource vocabulary embedded in {@link
 * Session#resources()}: a file, a memory store, or a GitHub repository,
 * discriminated by {@code type}.
 *
 * <p>An embedded <em>datatype</em> union, not a {@link Resource} hierarchy:
 * none of its variants carry {@code resourceType}, only the {@code type}
 * sub-discriminator named in oasp-standard's {@code session.ts}.
 *
 * <p>{@code sealed} for the same reason {@link Event} is: a switch over
 * every permitted variant can be exhaustive, and {@link
 * UnknownSessionResource} is the forward-compatible catch-all for a
 * {@code type} this SDK version does not recognise.
 *
 * @see FileResource
 * @see MemoryStoreResource
 * @see GithubRepositoryResource
 * @see UnknownSessionResource
 */
public sealed interface SessionResource
        permits FileResource, MemoryStoreResource, GithubRepositoryResource, UnknownSessionResource {

    /**
     * The discriminant naming which kind of mountable resource this is,
     * e.g. {@code "file"}.
     */
    String type();
}
