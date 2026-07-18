package dev.oasp.client.types;

/**
 * Whether the interaction an {@link AuditEvent} records succeeded, failed,
 * or targeted a resource that never existed. {@code NOT_FOUND} is
 * deliberately distinct from {@code FAILURE}: a probe against a nonexistent
 * id must be distinguishable in the trail from an ordinary operational
 * failure (a server MUST NOT skip emitting an AuditEvent merely because its
 * precondition check found nothing to act on).
 */
public enum AuditOutcome {
    SUCCESS,
    FAILURE,
    NOT_FOUND
}
