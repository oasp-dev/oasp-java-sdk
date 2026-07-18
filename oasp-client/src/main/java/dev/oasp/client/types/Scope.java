package dev.oasp.client.types;

import java.util.Objects;

/**
 * A generalized-ownership attachment point: a taxonomy level plus the
 * identifier at that level. {@code AgentDefinition}, {@link Conversation},
 * {@code Credential}, and {@link AuditEvent} all attach to a scope rather
 * than a hardcoded owner type, and a {@link Principal} lists every scope it
 * is a member of.
 *
 * <p>An embedded <em>datatype</em>, not a {@link Resource}: it never carries
 * its own {@code resourceType} and is always dispatched by the field name
 * that embeds it (per {@code docs/spec/resources.md}).
 *
 * @param level which level of the tenant/workspace/user/group/role taxonomy
 *              this attachment point is at
 * @param id    identifier of the scoped entity at that level
 */
public record Scope(ScopeLevel level, String id) {

    public Scope {
        // Inbound data, embedded in multiple resources: lenient by the
        // convention this package follows throughout - non-null only, no
        // blank check - even though oasp-standard's own schema requires
        // non-blank on the wire.
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(id, "id");
    }
}
