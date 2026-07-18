package dev.oasp.client.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.RecordComponent;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * Guards against silent drift between this package's hand-written types and
 * oasp-standard's own generated JSON Schemas, vendored under {@code
 * src/test/resources/spec-schemas/}. For each resource this asserts (a) an
 * instance's {@code resourceType()} equals the schema's {@code
 * resourceType} const, and (b) the record's component names cover the
 * schema's {@code properties} and {@code required} set - flagging both a
 * field the spec has that we don't model, and a field we model that the
 * spec doesn't have.
 *
 * <p>{@code resourceType} (and, for {@link Event}, the {@code type}
 * sub-discriminator) are deliberately excluded from the field-coverage
 * check: per the {@link Resource}/{@link Event} Javadoc, these are exposed
 * as a domain <em>method</em>, not a stored record component - checked
 * separately, and directly, via (a).
 */
class SpecDriftTest {

    private static final Set<String> RESOURCE_TYPE_ONLY = Set.of("resourceType");
    private static final Set<String> RESOURCE_TYPE_AND_TYPE = Set.of("resourceType", "type");

    @Test
    void principalMatchesSpecSchema() {
        SpecSchema schema = SpecSchema.loadResource("Principal.json");
        Principal principal = new Principal(
                "principal-1",
                PrincipalKind.USER,
                new PrincipalIdentity("sub-1", Optional.empty(), Optional.empty(), Optional.empty()),
                List.of(),
                List.of());

        assertThat(principal.resourceType()).isEqualTo(schema.constOf("resourceType"));
        assertRecordCoversSchema(Principal.class, schema, RESOURCE_TYPE_ONLY);
        assertRecordCoversSchema(PrincipalIdentity.class, schema.nested("identity"), Set.of());
    }

    @Test
    void conversationMatchesSpecSchema() {
        SpecSchema schema = SpecSchema.loadResource("Conversation.json");
        Conversation conversation = new Conversation(
                "conv-1",
                new Scope(ScopeLevel.WORKSPACE, "ws-1"),
                new PrincipalRef(PrincipalKind.USER, "user-1"),
                "session-1",
                new AgentVersionRef("agent-1", 1L),
                List.of());

        assertThat(conversation.resourceType()).isEqualTo(schema.constOf("resourceType"));
        assertRecordCoversSchema(Conversation.class, schema, RESOURCE_TYPE_ONLY);
        assertRecordCoversSchema(Scope.class, schema.def("Scope"), Set.of());
        assertRecordCoversSchema(PrincipalRef.class, schema.def("PrincipalRef"), Set.of());
        assertRecordCoversSchema(AgentVersionRef.class, schema.def("AgentVersionRef"), Set.of());
    }

    @Test
    void sessionMatchesSpecSchema() {
        SpecSchema schema = SpecSchema.loadResource("Session.json");
        Session session = new Session("session-1", new AgentVersionRef("agent-1", 1L), List.of(), List.of());

        assertThat(session.resourceType()).isEqualTo(schema.constOf("resourceType"));
        assertRecordCoversSchema(Session.class, schema, RESOURCE_TYPE_ONLY);
    }

    @Test
    void auditEventMatchesSpecSchema() {
        SpecSchema schema = SpecSchema.loadResource("AuditEvent.json");
        AuditEvent event = new AuditEvent(
                "audit-1",
                new AuditWho(new PrincipalRef(PrincipalKind.USER, "user-1"), Optional.empty()),
                AuditInteraction.CREATE_CONVERSATION,
                Optional.empty(),
                Instant.parse("2026-01-01T00:00:00Z"),
                AuditOutcome.SUCCESS,
                Optional.empty(),
                new AuditRefs(Optional.empty(), Optional.empty(), Optional.empty(), List.of()),
                Optional.empty());

        assertThat(event.resourceType()).isEqualTo(schema.constOf("resourceType"));
        assertRecordCoversSchema(AuditEvent.class, schema, RESOURCE_TYPE_ONLY);
        assertRecordCoversSchema(AuditWho.class, schema.nested("who"), Set.of());
        assertRecordCoversSchema(AuditRefs.class, schema.nested("refs"), Set.of());
        assertRecordCoversSchema(AuditEvidence.class, schema.nested("evidence"), Set.of());

        assertEnumMatches(AuditInteraction.class, schema.nested("what").enumValues());
        assertEnumMatches(AuditOutcome.class, schema.nested("outcome").enumValues());
    }

    @Test
    void eventVariantsMatchSpecSchema() throws ClassNotFoundException {
        SpecSchema schema = SpecSchema.loadResource("Event.json");
        assertThat(schema.variants()).hasSize(8);

        for (SpecSchema variant : schema.variants()) {
            String wireType = variant.constOf("type");
            Class<?> variantClass = Class.forName("dev.oasp.client.types." + eventClassNameFor(wireType));

            assertThat(Event.class).as("%s must implement Event", variantClass).isAssignableFrom(variantClass);
            assertRecordCoversSchema(variantClass, variant, RESOURCE_TYPE_AND_TYPE);

            if ("status".equals(wireType)) {
                assertEnumMatches(SessionRunStatus.class, variant.nested("status").enumValues());
            }
        }

        // resourceType() is a default method shared by every variant - one
        // representative instance is enough to confirm it matches the spec.
        Event event = new AssistantMessageStartEvent("evt-1", Instant.parse("2026-01-01T00:00:00Z"), "msg-1");
        assertThat(event.resourceType()).isEqualTo(schema.variants().get(0).constOf("resourceType"));
    }

    @Test
    void sharedEnumsMatchSpecSchema() {
        SpecSchema conversation = SpecSchema.loadResource("Conversation.json");
        assertEnumMatches(PrincipalKind.class, conversation.def("PrincipalKind").enumValues());
        assertEnumMatches(
                ScopeLevel.class, conversation.def("Scope").nested("level").enumValues());
    }

    private static void assertRecordCoversSchema(Class<?> recordType, SpecSchema schema, Set<String> ignored) {
        Set<String> specProperties = withoutIgnored(schema.propertyNames(), ignored);
        Set<String> specRequired = withoutIgnored(schema.requiredNames(), ignored);
        Set<String> javaComponents = Arrays.stream(recordType.getRecordComponents())
                .map(RecordComponent::getName)
                .collect(Collectors.toSet());

        Set<String> missingFromRecord = difference(specProperties, javaComponents);
        Set<String> extraOnRecord = difference(javaComponents, specProperties);
        Set<String> missingRequiredField = difference(specRequired, javaComponents);

        assertThat(missingFromRecord)
                .as("%s is missing fields the spec declares", recordType.getSimpleName())
                .isEmpty();
        assertThat(extraOnRecord)
                .as("%s declares fields the spec doesn't have", recordType.getSimpleName())
                .isEmpty();
        assertThat(missingRequiredField)
                .as("%s is missing required fields", recordType.getSimpleName())
                .isEmpty();
    }

    private static void assertEnumMatches(Class<? extends Enum<?>> enumType, List<String> wireValues) {
        Set<String> expected = wireValues.stream().map(SpecDriftTest::toEnumConstantName).collect(Collectors.toSet());
        Set<String> actual =
                Arrays.stream(enumType.getEnumConstants()).map(Enum::name).collect(Collectors.toSet());

        assertThat(actual).as("%s must match the spec's enum values", enumType.getSimpleName()).isEqualTo(expected);
    }

    /** {@code "createConversation"} / {@code "not_found"} -> {@code "CREATE_CONVERSATION"} / {@code "NOT_FOUND"}. */
    private static String toEnumConstantName(String wireValue) {
        StringBuilder result = new StringBuilder();
        for (char c : wireValue.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append('_').append(c);
            } else {
                result.append(Character.toUpperCase(c));
            }
        }
        return result.toString();
    }

    /** {@code "assistant_message_start"} -> {@code "AssistantMessageStartEvent"}. */
    private static String eventClassNameFor(String wireType) {
        return Arrays.stream(wireType.split("_"))
                .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
                .collect(Collectors.joining())
                + "Event";
    }

    private static Set<String> withoutIgnored(Set<String> names, Set<String> ignored) {
        return names.stream().filter(name -> !ignored.contains(name)).collect(Collectors.toSet());
    }

    private static Set<String> difference(Set<String> from, Set<String> subtract) {
        return from.stream().filter(name -> !subtract.contains(name)).collect(Collectors.toSet());
    }
}
