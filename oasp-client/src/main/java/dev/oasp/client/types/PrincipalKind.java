package dev.oasp.client.types;

import dev.oasp.client.json.JsonException;

/**
 * The kind of acting party at OASP's identity plane: a human user, a
 * machine-to-machine service, or an agent acting on its own behalf.
 *
 * <p>Each constant carries its own wire value alongside the Java constant
 * name, since the two differ in casing (e.g. {@code USER} <-> {@code
 * "user"}) - (de)serialization must go through {@link #wireValue()}/{@link
 * #fromWire(String)}, never {@link #name()}/{@code valueOf}.
 *
 * @see Principal
 * @see PrincipalRef
 */
public enum PrincipalKind {
    USER("user"),
    SERVICE("service"),
    AGENT("agent");

    private final String wireValue;

    PrincipalKind(String wireValue) {
        this.wireValue = wireValue;
    }

    /** This constant's wire representation, e.g. {@code "user"}. */
    public String wireValue() {
        return wireValue;
    }

    /**
     * Looks up the constant for a wire value.
     *
     * @throws JsonException if {@code wireValue} matches no constant
     */
    public static PrincipalKind fromWire(String wireValue) {
        for (PrincipalKind kind : values()) {
            if (kind.wireValue.equals(wireValue)) {
                return kind;
            }
        }
        throw new JsonException("Unrecognised PrincipalKind value: \"" + wireValue + "\"");
    }
}
