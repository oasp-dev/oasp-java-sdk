package dev.oasp.client.types;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A thin, read-only view over one vendored oasp-standard JSON Schema
 * document (parsed by {@link MinimalJson}), exposing exactly the shapes
 * {@link SpecDriftTest} needs to check: a resource's own {@code
 * resourceType}/{@code type} discriminator, its declared {@code
 * properties}/{@code required}, its {@code oneOf} variants (for {@code
 * Event}), and its {@code $defs} (for shared datatypes like {@code Scope}).
 */
final class SpecSchema {

    private final Map<String, Object> node;

    private SpecSchema(Map<String, Object> node) {
        this.node = node;
    }

    @SuppressWarnings("unchecked")
    static SpecSchema loadResource(String resourceName) {
        String path = "/spec-schemas/" + resourceName;
        try (InputStream in = SpecSchema.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Vendored spec schema not found on classpath: " + path);
            }
            String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return new SpecSchema((Map<String, Object>) MinimalJson.parse(json));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** This schema's own variants: itself, or every {@code oneOf} branch. */
    @SuppressWarnings("unchecked")
    List<SpecSchema> variants() {
        Object oneOf = node.get("oneOf");
        if (oneOf == null) {
            return List.of(this);
        }
        return ((List<Object>) oneOf)
                .stream().map(branch -> new SpecSchema((Map<String, Object>) branch)).toList();
    }

    Set<String> propertyNames() {
        return properties().keySet();
    }

    @SuppressWarnings("unchecked")
    Set<String> requiredNames() {
        List<Object> required = (List<Object>) node.get("required");
        return required == null
                ? Set.of()
                : required.stream().map(String.class::cast).collect(Collectors.toSet());
    }

    /** The {@code const} declared on {@code properties.<name>}, if any. */
    @SuppressWarnings("unchecked")
    String constOf(String propertyName) {
        return (String) ((Map<String, Object>) properties().get(propertyName)).get("const");
    }

    /** The inline schema fragment at {@code properties.<name>}. */
    @SuppressWarnings("unchecked")
    SpecSchema nested(String propertyName) {
        return new SpecSchema((Map<String, Object>) properties().get(propertyName));
    }

    /** The named entry under this schema's {@code $defs}. */
    @SuppressWarnings("unchecked")
    SpecSchema def(String name) {
        Map<String, Object> defs = (Map<String, Object>) node.get("$defs");
        return new SpecSchema((Map<String, Object>) defs.get(name));
    }

    /** This schema fragment's own {@code enum} values, e.g. an inline status enum. */
    @SuppressWarnings("unchecked")
    List<String> enumValues() {
        List<Object> values = (List<Object>) node.get("enum");
        return values.stream().map(String.class::cast).toList();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> properties() {
        return (Map<String, Object>) node.get("properties");
    }
}
