package dev.oasp.client.types;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * The normative, non-negotiable audit record OASP requires for every
 * mutating interaction (FHIR {@code AuditEvent} is the prior art and the
 * posture: an implementation that cannot answer "what did the agent do as
 * {member} on {date}" is non-conformant).
 *
 * <p>Unlike this SDK's original, invented model - a sealed hierarchy of
 * {@code ConversationCreated}/{@code ConversationClosed} variants - the real
 * oasp-standard {@code auditEventSchema} is a single flat shape: {@link
 * #what()} is a plain, closed seven-value enum field on one object, not a
 * structural discriminated union. There is therefore no per-{@code what}
 * variance to model with a sealed interface, and no {@code UnknownAuditEvent}
 * fallback is needed here - {@code what} is a data field, not a shape
 * discriminator. (This is exactly why {@link Resource} exists one level up:
 * {@code resourceType: "AuditEvent"} is the real, self-describing
 * discriminator; {@code what} sub-discriminates beneath it.)
 *
 * <p>Deliberately lenient, matching this package's convention elsewhere: the
 * spec's cross-field rule - {@code scope} required unless {@code outcome}
 * is {@code NOT_FOUND} - is not enforced in this compact constructor. The
 * server is the source of truth for that relationship; rejecting it here
 * risks turning an otherwise-valid response into a construction failure.
 *
 * @param id       unique identifier of this AuditEvent
 * @param who      the acting principal, and who it acted on behalf of, if any
 * @param what     which v0 interaction this event records
 * @param scope    the attachment point the interaction occurred within
 * @param when     when the interaction occurred
 * @param outcome  whether the interaction succeeded, failed, or not-found
 * @param degraded whether the interaction completed in a degraded mode
 * @param refs     references to the session/conversation/definition/credentials involved
 * @param evidence action-specific evidence, when resolvable
 */
public record AuditEvent(
        String id,
        AuditWho who,
        AuditInteraction what,
        Optional<Scope> scope,
        Instant when,
        AuditOutcome outcome,
        Optional<Boolean> degraded,
        AuditRefs refs,
        Optional<AuditEvidence> evidence)
        implements Resource {

    public AuditEvent {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(who, "who");
        Objects.requireNonNull(what, "what");
        Objects.requireNonNull(when, "when");
        Objects.requireNonNull(outcome, "outcome");
        Objects.requireNonNull(refs, "refs");

        scope = (scope == null) ? Optional.empty() : scope;
        degraded = (degraded == null) ? Optional.empty() : degraded;
        evidence = (evidence == null) ? Optional.empty() : evidence;
    }

    @Override
    public String resourceType() {
        return "AuditEvent";
    }
}
