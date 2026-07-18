package dev.oasp.client.types;

import java.util.List;
import java.util.Optional;

/**
 * References tying an {@link AuditEvent} back to the resources involved.
 * Every field is optional - not every interaction touches every resource
 * type (e.g. {@code publish} touches a definition but no session). On a
 * {@code not_found} outcome, the relevant id is still populated here even
 * though the resource it names never existed: it is the caller's own
 * asserted target, not new information the event discloses.
 *
 * <p>An embedded <em>datatype</em>, private to {@link AuditEvent}.
 *
 * @param sessionId      identifier of the Session involved, if any
 * @param conversationId identifier of the Conversation involved, if any
 * @param definitionId   identifier of the AgentDefinition involved, if any
 * @param credentialIds  identifiers of the Credentials attached/used in
 *                       this interaction; never {@code null}, may be empty -
 *                       an absent array on the wire normalizes to empty
 *                       rather than {@code Optional<List<...>>}, since the
 *                       two carry the same "nothing here" meaning
 */
public record AuditRefs(
        Optional<String> sessionId,
        Optional<String> conversationId,
        Optional<String> definitionId,
        List<String> credentialIds) {

    public AuditRefs {
        sessionId = (sessionId == null) ? Optional.empty() : sessionId;
        conversationId = (conversationId == null) ? Optional.empty() : conversationId;
        definitionId = (definitionId == null) ? Optional.empty() : definitionId;
        credentialIds = (credentialIds == null) ? List.of() : List.copyOf(credentialIds);
    }
}
