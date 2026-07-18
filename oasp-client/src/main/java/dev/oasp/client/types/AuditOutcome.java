package dev.oasp.client.types;

import dev.oasp.client.json.JsonException;

/**
 * Whether the interaction an {@link AuditEvent} records succeeded, failed,
 * or targeted a resource that never existed. {@code NOT_FOUND} is
 * deliberately distinct from {@code FAILURE}: a probe against a nonexistent
 * id must be distinguishable in the trail from an ordinary operational
 * failure (a server MUST NOT skip emitting an AuditEvent merely because its
 * precondition check found nothing to act on).
 *
 * <p>(De)serialization goes through {@link #wireValue()}/{@link
 * #fromWire(String)}, never {@link #name()}/{@code valueOf} - see {@link
 * PrincipalKind} for why.
 */
public enum AuditOutcome {
    SUCCESS("success"),
    FAILURE("failure"),
    NOT_FOUND("not_found");

    private final String wireValue;

    AuditOutcome(String wireValue) {
        this.wireValue = wireValue;
    }

    public String wireValue() {
        return wireValue;
    }

    /**
     * Looks up the constant for a wire value.
     *
     * @throws JsonException if {@code wireValue} matches no constant
     */
    public static AuditOutcome fromWire(String wireValue) {
        for (AuditOutcome outcome : values()) {
            if (outcome.wireValue.equals(wireValue)) {
                return outcome;
            }
        }
        throw new JsonException("Unrecognised AuditOutcome value: \"" + wireValue + "\"");
    }
}
