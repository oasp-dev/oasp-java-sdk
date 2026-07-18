package dev.oasp.client.json;

import dev.oasp.client.types.AuditEvent;
import dev.oasp.client.types.AuditEvidence;
import dev.oasp.client.types.AuditRefs;
import dev.oasp.client.types.AuditWho;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Record -> tree writers for {@link AuditEvent} and its private embedded
 * datatypes ({@link AuditWho}, {@link AuditRefs}, {@link AuditEvidence}).
 * {@code what} is written as a plain enum field (see {@link
 * AuditEvent#what()}'s Javadoc) - there is no structural discriminator here,
 * unlike {@code Event} one type over.
 */
final class AuditWriters {

    private AuditWriters() {}

    static Map<String, Object> writeAuditEvent(AuditEvent event) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("resourceType", event.resourceType());
        tree.put("id", event.id());
        tree.put("who", writeAuditWho(event.who()));
        tree.put("what", event.what().wireValue());
        ProtocolTreeWriter.putIfPresent(tree, "scope", event.scope().map(DatatypeWriters::writeScope));
        tree.put("when", event.when().toString());
        tree.put("outcome", event.outcome().wireValue());
        ProtocolTreeWriter.putIfPresent(tree, "degraded", event.degraded());
        tree.put("refs", writeAuditRefs(event.refs()));
        ProtocolTreeWriter.putIfPresent(tree, "evidence", event.evidence().map(AuditWriters::writeAuditEvidence));
        return tree;
    }

    static Map<String, Object> writeAuditWho(AuditWho who) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("principal", DatatypeWriters.writePrincipalRef(who.principal()));
        ProtocolTreeWriter.putIfPresent(tree, "onBehalfOf", who.onBehalfOf().map(DatatypeWriters::writePrincipalRef));
        return tree;
    }

    static Map<String, Object> writeAuditRefs(AuditRefs refs) {
        Map<String, Object> tree = new LinkedHashMap<>();
        ProtocolTreeWriter.putIfPresent(tree, "sessionId", refs.sessionId());
        ProtocolTreeWriter.putIfPresent(tree, "conversationId", refs.conversationId());
        ProtocolTreeWriter.putIfPresent(tree, "definitionId", refs.definitionId());
        tree.put("credentialIds", refs.credentialIds());
        return tree;
    }

    static Map<String, Object> writeAuditEvidence(AuditEvidence evidence) {
        Map<String, Object> tree = new LinkedHashMap<>();
        ProtocolTreeWriter.putIfPresent(tree, "contentDigest", evidence.contentDigest());
        ProtocolTreeWriter.putIfPresent(
                tree, "agentVersionRef", evidence.agentVersionRef().map(DatatypeWriters::writeAgentVersionRef));
        return tree;
    }
}
