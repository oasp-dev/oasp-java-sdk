package dev.oasp.client.types;

import java.util.Objects;

/**
 * A pin to one specific version of an {@code AgentDefinition}, keyed into an
 * immutable {@code AgentDefinitionVersion} content snapshot. {@link
 * Conversation} and {@link Session} are both created pinned to one of
 * these, and {@link AuditEvent}'s {@code evidence} may record one too.
 *
 * <p>An embedded <em>datatype</em>, not a {@link Resource}.
 *
 * @param agentDefinitionId identifier of the pinned {@code AgentDefinition}
 * @param version           the pinned version number; positive per the
 *                          spec, but - like every other inbound field in
 *                          this package - only checked here for non-null,
 *                          not range
 */
public record AgentVersionRef(String agentDefinitionId, long version) {

    public AgentVersionRef {
        Objects.requireNonNull(agentDefinitionId, "agentDefinitionId");
    }
}
