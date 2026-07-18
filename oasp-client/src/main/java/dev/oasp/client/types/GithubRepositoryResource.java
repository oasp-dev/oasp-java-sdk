package dev.oasp.client.types;

import java.util.Objects;
import java.util.Optional;

/**
 * A GitHub repository mounted into a {@link Session} at create.
 *
 * @param owner owner (user or organization) of the mounted repository
 * @param repo  name of the mounted repository
 * @param ref   branch, tag, or commit SHA to mount; if absent, the provider
 *              mounts the default branch
 */
public record GithubRepositoryResource(String owner, String repo, Optional<String> ref)
        implements SessionResource {

    public GithubRepositoryResource {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(repo, "repo");
        ref = (ref == null) ? Optional.empty() : ref;
    }

    @Override
    public String type() {
        return "github_repository";
    }
}
