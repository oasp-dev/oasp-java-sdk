package dev.oasp.client.types;

import java.util.List;
import java.util.Objects;

/**
 * The request body for {@code POST /conversations} - {@code
 * createConversation}, which mints the first {@link Session} a brand-new
 * {@link Conversation} ever rides on.
 *
 * <p><strong>This shape is not yet normatively specified upstream.</strong>
 * oasp-standard's generated {@code openapi/oasp-v1alpha1.yaml} marks this
 * operation a placeholder ("full contract is docs/spec/interactions.md §
 * createConversation"), and that document only states the server's
 * resulting <em>behaviour</em>, not a request-body schema. The fields below
 * are this SDK's best-effort inference from that normative text:
 *
 * <ul>
 *   <li>{@code scope} - {@code auditEventSchema}'s doc comment says
 *       {@code createConversation}'s {@code scope} "comes from the
 *       caller-supplied input rather than an existing resource", so the
 *       caller must supply it;
 *   <li>{@code initiatingPrincipal} - {@link Conversation#initiatingPrincipal()}
 *       is a required field on the resulting resource with no other stated
 *       source;
 *   <li>{@code definitionId} - interactions.md requires resolving "the
 *       target AgentDefinition's current publishedVersion", so the caller
 *       must name which AgentDefinition; {@code definitionId} matches the
 *       field name {@code auditRefsSchema} already uses for this identifier;
 *   <li>{@code resources} - interactions.md requires mounting "the new
 *       Session's resources[] exactly as given by the caller".
 * </ul>
 *
 * {@code vaultIds} is deliberately absent: the spec has the server
 * <em>resolve</em> vault attachment from tool grants, not accept it from
 * the caller. Revisit this type once oasp-standard pins down the actual
 * request schema.
 *
 * @param scope               the attachment point for the new Conversation
 * @param initiatingPrincipal the Principal the new Conversation is started by
 * @param definitionId        identifier of the target AgentDefinition to pin against
 * @param resources           resources to mount into the initial Session
 */
public record CreateConversation(
        Scope scope, PrincipalRef initiatingPrincipal, String definitionId, List<SessionResource> resources) {

    public CreateConversation {
        // This is an outbound request we construct, unlike the rest of this
        // package's inbound resources - so, per this package's convention,
        // its own identifier gets a blank check in addition to non-null.
        Objects.requireNonNull(scope, "scope");
        Objects.requireNonNull(initiatingPrincipal, "initiatingPrincipal");
        Objects.requireNonNull(definitionId, "definitionId");
        if (definitionId.isBlank()) {
            throw new IllegalArgumentException("definitionId must not be blank");
        }
        Objects.requireNonNull(resources, "resources");

        resources = List.copyOf(resources);
    }
}
