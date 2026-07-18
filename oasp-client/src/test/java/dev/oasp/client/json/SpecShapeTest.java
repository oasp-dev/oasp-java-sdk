package dev.oasp.client.json;

import static org.assertj.core.api.Assertions.assertThat;

import dev.oasp.client.types.AgentVersionRef;
import dev.oasp.client.types.AssistantMessageStartEvent;
import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.Principal;
import dev.oasp.client.types.PrincipalIdentity;
import dev.oasp.client.types.PrincipalKind;
import dev.oasp.client.types.PrincipalRef;
import dev.oasp.client.types.Scope;
import dev.oasp.client.types.ScopeLevel;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * A light structural check that the JSON this codec actually emits carries
 * the field names (and {@code resourceType}) the vendored oasp-standard
 * schema declares, complementing {@code SpecDriftTest} in {@code
 * dev.oasp.client.types} - which only checks the Java record shapes those
 * schemas were reviewed against, not what this codec writes on the wire.
 */
class SpecShapeTest {

    private final HandRolledJsonCodec codec = new HandRolledJsonCodec();

    @Test
    void principalEmitsSpecFieldNamesAndResourceType() {
        Principal principal = new Principal(
                "principal-1",
                PrincipalKind.USER,
                new PrincipalIdentity("sub-1", Optional.empty(), Optional.empty(), Optional.empty()),
                List.of(),
                List.of());

        Map<String, Object> tree = writtenTree(principal);
        Map<String, Object> properties = propertiesOf(loadSchema("Principal.json"));

        assertThat(tree.get("resourceType")).isEqualTo(constOf(properties, "resourceType"));
        assertThat(tree.keySet()).isEqualTo(properties.keySet());
    }

    @Test
    void conversationEmitsSpecFieldNamesAndResourceType() {
        Conversation conversation = new Conversation(
                "conv-1",
                new Scope(ScopeLevel.WORKSPACE, "ws-1"),
                new PrincipalRef(PrincipalKind.USER, "user-1"),
                "session-1",
                new AgentVersionRef("agent-1", 1L),
                List.of());

        Map<String, Object> tree = writtenTree(conversation);
        Map<String, Object> properties = propertiesOf(loadSchema("Conversation.json"));

        assertThat(tree.get("resourceType")).isEqualTo(constOf(properties, "resourceType"));
        assertThat(tree.keySet()).isEqualTo(properties.keySet());
    }

    @Test
    void eventVariantEmitsSpecFieldNamesAndResourceType() {
        var event = new AssistantMessageStartEvent("evt-1", Instant.parse("2026-01-01T00:00:00Z"), "msg-1");

        Map<String, Object> tree = writtenTree(event);
        Map<String, Object> properties = propertiesOf(eventVariant("assistant_message_start"));

        assertThat(tree.get("resourceType")).isEqualTo(constOf(properties, "resourceType"));
        assertThat(tree.keySet()).isEqualTo(properties.keySet());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> writtenTree(Object value) {
        return (Map<String, Object>) JsonParser.parse(codec.write(value));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> propertiesOf(Map<String, Object> schema) {
        return (Map<String, Object>) schema.get("properties");
    }

    @SuppressWarnings("unchecked")
    private static String constOf(Map<String, Object> properties, String propertyName) {
        return (String) ((Map<String, Object>) properties.get(propertyName)).get("const");
    }

    /** The {@code oneOf} branch of {@code Event.json} whose {@code type} const is {@code wireType}. */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> eventVariant(String wireType) {
        List<Object> variants = (List<Object>) loadSchema("Event.json").get("oneOf");
        for (Object variantObj : variants) {
            Map<String, Object> variant = (Map<String, Object>) variantObj;
            if (wireType.equals(constOf(propertiesOf(variant), "type"))) {
                return variant;
            }
        }
        throw new IllegalStateException("No Event.json variant with type const: " + wireType);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> loadSchema(String resourceName) {
        String path = "/spec-schemas/" + resourceName;
        try (InputStream in = SpecShapeTest.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Vendored spec schema not found on classpath: " + path);
            }
            String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return (Map<String, Object>) JsonParser.parse(json);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
