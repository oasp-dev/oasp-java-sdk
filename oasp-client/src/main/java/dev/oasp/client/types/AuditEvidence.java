package dev.oasp.client.types;

import java.util.Optional;

/**
 * Action-specific evidence an {@link AuditEvent} may carry beyond bare
 * resource references - answers <em>what</em> happened, not just
 * <em>which</em> resource was involved. Optional and additive: v0 defines
 * exactly these two evidence kinds; a richer evidence set is deferred
 * beyond this slice.
 *
 * <p>An embedded <em>datatype</em>, private to {@link AuditEvent}.
 *
 * @param contentDigest   a canonical digest of the message content posted
 *                        by {@code send}, formatted {@code sha256:<hex>};
 *                        populated only on {@code send} events
 * @param agentVersionRef the AgentDefinition version pinned - or, for
 *                        {@code migrate}, being attempted - at the time of
 *                        this interaction
 */
public record AuditEvidence(Optional<String> contentDigest, Optional<AgentVersionRef> agentVersionRef) {

    public AuditEvidence {
        contentDigest = (contentDigest == null) ? Optional.empty() : contentDigest;
        agentVersionRef = (agentVersionRef == null) ? Optional.empty() : agentVersionRef;
    }
}
