package dev.oasp.client.types;

/**
 * The five levels an OASP {@link Scope} may attach at, from broadest to
 * narrowest. Normative resolution order when scopes overlap is
 * {@code user > role > group > workspace > tenant} (most-specific-scope-wins).
 */
public enum ScopeLevel {
    TENANT,
    WORKSPACE,
    USER,
    GROUP,
    ROLE
}
