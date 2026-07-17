package dev.oasp.client.types;

import java.util.Objects;

/**
 * A single claim that a {@link Principal} holds: "I have access at {@code level}
 * to the entity identified by {@code id}". A principal typically carries several
 * of these (see {@link Principal#claims()}).
 *
 * @param level the kind of entity this claim covers
 * @param id    the identifier of that entity, e.g. a tenant id or user id
 */
public record ScopeClaim(ScopeLevel level, String id) {

    // Compact constructor: runs before the (implicit) field assignment, so it's
    // the idiomatic place to validate a record's components. Throwing here
    // means an invalid ScopeClaim can never exist.
    public ScopeClaim {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(id, "id");
        if (id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
    }
}
