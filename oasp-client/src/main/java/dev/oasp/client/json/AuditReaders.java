package dev.oasp.client.json;

import dev.oasp.client.types.AuditEvent;
import dev.oasp.client.types.AuditEvidence;
import dev.oasp.client.types.AuditInteraction;
import dev.oasp.client.types.AuditOutcome;
import dev.oasp.client.types.AuditRefs;
import dev.oasp.client.types.AuditWho;
import java.util.Map;

/** Tree -> record readers mirroring {@link AuditWriters}. */
final class AuditReaders {

    private AuditReaders() {}

    static AuditEvent mapAuditEvent(Map<String, Object> obj) {
        var id = JsonFields.string(obj, "id");
        var who = mapAuditWho(JsonFields.object(obj, "who"));
        var what = JsonFields.enumValue(obj, "what", AuditInteraction::fromWire);
        var scope = JsonFields.optionalObject(obj, "scope").map(DatatypeReaders::mapScope);
        var when = JsonFields.instant(obj, "when");
        var outcome = JsonFields.enumValue(obj, "outcome", AuditOutcome::fromWire);
        var degraded = JsonFields.optionalBoolean(obj, "degraded");
        var refs = mapAuditRefs(JsonFields.object(obj, "refs"));
        var evidence = JsonFields.optionalObject(obj, "evidence").map(AuditReaders::mapAuditEvidence);
        return new AuditEvent(id, who, what, scope, when, outcome, degraded, refs, evidence);
    }

    static AuditWho mapAuditWho(Map<String, Object> obj) {
        var principal = DatatypeReaders.mapPrincipalRef(JsonFields.object(obj, "principal"));
        var onBehalfOf = JsonFields.optionalObject(obj, "onBehalfOf").map(DatatypeReaders::mapPrincipalRef);
        return new AuditWho(principal, onBehalfOf);
    }

    static AuditRefs mapAuditRefs(Map<String, Object> obj) {
        var sessionId = JsonFields.optionalString(obj, "sessionId");
        var conversationId = JsonFields.optionalString(obj, "conversationId");
        var definitionId = JsonFields.optionalString(obj, "definitionId");
        var credentialIds = JsonFields.arrayOrEmpty(obj, "credentialIds").stream()
                .map(node -> JsonTrees.asString(node, "credentialIds[]"))
                .toList();
        return new AuditRefs(sessionId, conversationId, definitionId, credentialIds);
    }

    static AuditEvidence mapAuditEvidence(Map<String, Object> obj) {
        var contentDigest = JsonFields.optionalString(obj, "contentDigest");
        var agentVersionRef =
                JsonFields.optionalObject(obj, "agentVersionRef").map(DatatypeReaders::mapAgentVersionRef);
        return new AuditEvidence(contentDigest, agentVersionRef);
    }
}
