package dev.oasp.client.types;

import java.util.List;
import java.util.Objects;

/**
 * The durable, user-facing thread - the "warp" held under tension while
 * {@link Session}s, the "weft", come and go. A Conversation outlives any
 * single Session: it tracks the {@code currentSessionId} riding on it
 * today, the {@code pinnedAgentVersion} that session was minted against,
 * and every {@code previousSessionIds} this Conversation has ridden on
 * before - the succession {@code migrate} appends to on each session
 * upgrade.
 *
 * @param id                  unique identifier of this Conversation
 * @param scope               the generalized-ownership attachment point
 *                            this Conversation belongs to
 * @param initiatingPrincipal the Principal that started this Conversation
 * @param currentSessionId    identifier of the Session this Conversation
 *                            currently rides on
 * @param pinnedAgentVersion  the immutable AgentDefinition version the
 *                            current session was minted against
 * @param previousSessionIds  identifiers of every Session this Conversation
 *                            has ridden on before the current one, oldest
 *                            first; never {@code null} and never contains
 *                            {@code null} elements, but may be empty
 */
public record Conversation(
        String id,
        Scope scope,
        PrincipalRef initiatingPrincipal,
        String currentSessionId,
        AgentVersionRef pinnedAgentVersion,
        List<String> previousSessionIds)
        implements Resource {

    public Conversation {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(scope, "scope");
        Objects.requireNonNull(initiatingPrincipal, "initiatingPrincipal");
        Objects.requireNonNull(currentSessionId, "currentSessionId");
        Objects.requireNonNull(pinnedAgentVersion, "pinnedAgentVersion");
        Objects.requireNonNull(previousSessionIds, "previousSessionIds");

        previousSessionIds = List.copyOf(previousSessionIds);
    }

    @Override
    public String resourceType() {
        return "Conversation";
    }
}
