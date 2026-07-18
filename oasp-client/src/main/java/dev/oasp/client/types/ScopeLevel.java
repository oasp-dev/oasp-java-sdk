package dev.oasp.client.types;

import dev.oasp.client.json.JsonException;

/**
 * The five levels an OASP {@link Scope} may attach at, from broadest to
 * narrowest. Normative resolution order when scopes overlap is
 * {@code user > role > group > workspace > tenant} (most-specific-scope-wins).
 *
 * <p>(De)serialization goes through {@link #wireValue()}/{@link
 * #fromWire(String)}, never {@link #name()}/{@code valueOf} - see {@link
 * PrincipalKind} for why.
 */
public enum ScopeLevel {
    TENANT("tenant"),
    WORKSPACE("workspace"),
    USER("user"),
    GROUP("group"),
    ROLE("role");

    private final String wireValue;

    ScopeLevel(String wireValue) {
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
    public static ScopeLevel fromWire(String wireValue) {
        for (ScopeLevel level : values()) {
            if (level.wireValue.equals(wireValue)) {
                return level;
            }
        }
        throw new JsonException("Unrecognised ScopeLevel value: \"" + wireValue + "\"");
    }
}
