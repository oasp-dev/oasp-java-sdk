package dev.oasp.client.types;

/**
 * Which of the seven v0 interactions an {@link AuditEvent} records. A
 * conformant server emits exactly one AuditEvent per interaction call,
 * including {@code stream} - a read path, audited under the FHIR {@code
 * AuditEvent} posture ("what did the agent do, or have observed of it").
 */
public enum AuditInteraction {
    PUBLISH,
    CREATE_CONVERSATION,
    MIGRATE,
    DRAIN,
    STREAM,
    SEND,
    SEND_TOOL_RESULT
}
