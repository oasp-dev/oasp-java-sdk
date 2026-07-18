package dev.oasp.client.types;

import java.util.List;
import java.util.Objects;

/**
 * A provider execution context: the disposable "weft" that rides across the
 * durable {@link Conversation} "warp". A Session is created pinned to one
 * agent version, with its {@code resources} mounted and its {@code
 * vaultIds} attached, and carries nothing forward from there - remounting
 * for a new version is {@code migrate}'s job, not something a Session does
 * on its own.
 *
 * <p>Unlike an earlier, invented version of this type, the real
 * oasp-standard {@code sessionSchema} carries no lifecycle-state field at
 * all (no {@code ACTIVE}/{@code DRAINING}/{@code DRAINED} enum) - a Session
 * is just its pin, its mounts, and its vaults.
 *
 * @param id                 unique identifier of this Session
 * @param pinnedAgentVersion the immutable AgentDefinition version this
 *                           Session was created against
 * @param resources          resources mounted into the session at create;
 *                           never {@code null} and never contains {@code
 *                           null} elements, but may be empty
 * @param vaultIds           identifiers of the Credentials attached to this
 *                           session, matched to MCP servers by URL at
 *                           creation; same non-null guarantee as above
 */
public record Session(
        String id, AgentVersionRef pinnedAgentVersion, List<SessionResource> resources, List<String> vaultIds)
        implements Resource {

    public Session {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(pinnedAgentVersion, "pinnedAgentVersion");
        Objects.requireNonNull(resources, "resources");
        Objects.requireNonNull(vaultIds, "vaultIds");

        resources = List.copyOf(resources);
        vaultIds = List.copyOf(vaultIds);
    }

    @Override
    public String resourceType() {
        return "Session";
    }
}
