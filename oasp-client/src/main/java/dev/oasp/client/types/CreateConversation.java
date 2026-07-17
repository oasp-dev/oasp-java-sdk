package dev.oasp.client.types;

import java.util.Objects;

/**
 * The request body for creating a new {@link Conversation}: just the
 * principal to open it on behalf of.
 *
 * @param principal the principal the new conversation will be opened for
 */
public record CreateConversation(Principal principal) {

    public CreateConversation {
        Objects.requireNonNull(principal, "principal");
    }

    /**
     * Factory mirroring the canonical constructor, for call sites that read
     * more naturally as {@code CreateConversation.forPrincipal(principal)}
     * than {@code new CreateConversation(principal)}.
     */
    public static CreateConversation forPrincipal(Principal principal) {
        return new CreateConversation(principal);
    }
}
