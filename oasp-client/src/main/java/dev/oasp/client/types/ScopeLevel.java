package dev.oasp.client.types;

/**
 * The kind of entity a {@link ScopeClaim} grants access to. OASP scopes a
 * conversation to one of these levels, from broadest (an entire tenant) to
 * narrowest (a single role).
 */
public enum ScopeLevel {
    TENANT,
    WORKSPACE,
    USER,
    GROUP,
    ROLE
}
