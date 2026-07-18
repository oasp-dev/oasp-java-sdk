package dev.oasp.client.types;

/**
 * The kind of acting party at OASP's identity plane: a human user, a
 * machine-to-machine service, or an agent acting on its own behalf.
 *
 * @see Principal
 * @see PrincipalRef
 */
public enum PrincipalKind {
    USER,
    SERVICE,
    AGENT
}
