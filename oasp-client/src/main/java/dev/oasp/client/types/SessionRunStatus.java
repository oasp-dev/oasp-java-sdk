package dev.oasp.client.types;

import dev.oasp.client.json.JsonException;

/**
 * The session's new status, as reported by a {@link StatusEvent}.
 *
 * <p>(De)serialization goes through {@link #wireValue()}/{@link
 * #fromWire(String)}, never {@link #name()}/{@code valueOf} - see {@link
 * PrincipalKind} for why.
 */
public enum SessionRunStatus {
    RUNNING("running"),
    IDLE("idle"),
    ERROR("error");

    private final String wireValue;

    SessionRunStatus(String wireValue) {
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
    public static SessionRunStatus fromWire(String wireValue) {
        for (SessionRunStatus status : values()) {
            if (status.wireValue.equals(wireValue)) {
                return status;
            }
        }
        throw new JsonException("Unrecognised SessionRunStatus value: \"" + wireValue + "\"");
    }
}
