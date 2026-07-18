package dev.oasp.client.types;

import dev.oasp.client.json.JsonException;

/**
 * Which of the seven v0 interactions an {@link AuditEvent} records. A
 * conformant server emits exactly one AuditEvent per interaction call,
 * including {@code stream} - a read path, audited under the FHIR {@code
 * AuditEvent} posture ("what did the agent do, or have observed of it").
 *
 * <p>Several wire values are camelCase (e.g. {@code "createConversation"}),
 * not a case transform of the Java constant name - (de)serialization goes
 * through {@link #wireValue()}/{@link #fromWire(String)}, never {@link
 * #name()}/{@code valueOf}.
 */
public enum AuditInteraction {
    PUBLISH("publish"),
    CREATE_CONVERSATION("createConversation"),
    MIGRATE("migrate"),
    DRAIN("drain"),
    STREAM("stream"),
    SEND("send"),
    SEND_TOOL_RESULT("sendToolResult");

    private final String wireValue;

    AuditInteraction(String wireValue) {
        this.wireValue = wireValue;
    }

    public String wireValue() {
        return wireValue;
    }

    /**
     * Looks up the constant for a wire value.
     *
     * @throws JsonException if {@code wireValue} matches no constant
     */
    public static AuditInteraction fromWire(String wireValue) {
        for (AuditInteraction interaction : values()) {
            if (interaction.wireValue.equals(wireValue)) {
                return interaction;
            }
        }
        throw new JsonException("Unrecognised AuditInteraction value: \"" + wireValue + "\"");
    }
}
