package dev.oasp.client.json;

import dev.oasp.client.types.AuditEvent;
import dev.oasp.client.types.Conversation;
import dev.oasp.client.types.ConversationClosed;
import dev.oasp.client.types.ConversationCreated;
import dev.oasp.client.types.ConversationState;
import dev.oasp.client.types.CreateConversation;
import dev.oasp.client.types.ErrorEnvelope;
import dev.oasp.client.types.Principal;
import dev.oasp.client.types.ScopeClaim;
import dev.oasp.client.types.ScopeLevel;
import dev.oasp.client.types.UnknownAuditEvent;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The default, zero-dependency {@link JsonCodec}: maps every type in {@code
 * dev.oasp.client.types} to/from JSON by hand, on top of {@link JsonWriter}
 * and {@link JsonParser}.
 *
 * <p>This does NOT attempt a generic, reflection-based mapper (the kind a
 * library like Jackson provides). The set of types that need mapping is
 * small and closed - it's exactly the types in {@code dev.oasp.client.types}
 * - so writing one explicit {@code case} per type is both simpler to follow
 * and safer than reflection: every mapping is visible right here, and the
 * compiler catches a missing case in the {@code switch} in {@link
 * #write(Object)}.
 *
 * <p>The JSON field names used below (e.g. {@code subject}, {@code
 * createdAt}) are the corresponding record component names, verbatim. Like
 * the {@link AuditEventTypes} discriminator strings, these are ASSUMED v0
 * names pending issue #2.
 */
final class HandRolledJsonCodec implements JsonCodec {

    @Override
    public String write(Object value) {
        // UnknownAuditEvent is a deliberate special case: it's written by
        // emitting its preserved rawJson verbatim rather than rebuilding
        // JSON text from a tree, so a round-trip of an event this SDK
        // version doesn't recognise is byte-for-byte faithful - not merely
        // "the same data in some equivalent JSON encoding" - to whatever
        // the server actually sent.
        if (value instanceof UnknownAuditEvent unknown) {
            return unknown.rawJson();
        }
        return JsonWriter.write(toTree(value));
    }

    @Override
    public <T> T read(String json, Class<T> type) {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        // Parsing (JSON text -> generic tree) is JsonParser's job and
        // already throws JsonException on malformed input; let that
        // propagate as-is.
        Object tree = JsonParser.parse(json);

        try {
            return type.cast(fromTree(tree, type, json));
        } catch (JsonException e) {
            // Already the right exception type (e.g. a field of the wrong
            // shape) - don't double-wrap it.
            throw e;
        } catch (RuntimeException e) {
            // Anything else unexpected during mapping - most commonly a
            // record's compact constructor rejecting a value we handed it
            // (e.g. a null where the record requires non-null) - is wrapped
            // as a JsonException so every failure from read() is one
            // consistent, documented exception type.
            throw new JsonException(
                    "Failed to read JSON as " + type.getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------
    // Writing: protocol type -> generic tree (Map/List/String/.../null)
    // ------------------------------------------------------------------

    /**
     * Converts a single value to the generic tree {@link JsonWriter}
     * understands. A {@code switch} with pattern matching over every
     * supported type, plus a {@code default} that rejects anything else -
     * see the class Javadoc for why this is explicit per-type mapping
     * rather than reflection.
     */
    private static Object toTree(Object value) {
        return switch (value) {
            case null -> null;

            // Values JsonWriter already understands natively pass through
            // unchanged.
            case String s -> s;
            case Boolean b -> b;
            case Long l -> l;
            case Double d -> d;

            // Instant has no JSON representation of its own; ISO-8601 text
            // (via Instant.toString(), e.g. "2026-01-01T00:00:00Z") is the
            // conventional, human-readable, sortable choice.
            case Instant instant -> instant.toString();

            // Optional is never itself a JSON concept - only Conversation's
            // closedAt component is Optional<Instant> (see the comment on
            // that record for why). An empty Optional writes as JSON null;
            // a present one writes as whatever its contents write as.
            case Optional<?> optional -> optional.isPresent() ? toTree(optional.get()) : null;

            // Enums write as their name(). Reading is the strict direction
            // (see mapEnum below) - writing has nothing to be strict about,
            // since a Java enum constant is always one of its known values.
            case ScopeLevel level -> level.name();
            case ConversationState state -> state.name();

            case Principal principal -> writePrincipal(principal);
            case ScopeClaim claim -> writeScopeClaim(claim);
            case Conversation conversation -> writeConversation(conversation);
            case CreateConversation createConversation -> writeCreateConversation(createConversation);
            case ErrorEnvelope errorEnvelope -> writeErrorEnvelope(errorEnvelope);
            case ConversationCreated created -> writeConversationCreated(created);
            case ConversationClosed closed -> writeConversationClosed(closed);

            // Reached only when an UnknownAuditEvent shows up *nested*
            // inside some other value being written (there's no such case
            // among the types this issue covers, but this keeps toTree
            // total rather than silently wrong if one ever does). The
            // top-level, byte-for-byte-faithful handling lives in write()
            // above; here we fall back to re-parsing the preserved rawJson
            // into a tree so it can still be embedded structurally.
            case UnknownAuditEvent unknown -> JsonParser.parse(unknown.rawJson());

            default ->
                    throw new JsonException(
                            "Cannot write value of unsupported type: " + value.getClass().getName());
        };
    }

    private static Map<String, Object> writePrincipal(Principal principal) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("subject", principal.subject());
        tree.put("claims", principal.claims().stream().map(HandRolledJsonCodec::writeScopeClaim).toList());
        return tree;
    }

    private static Map<String, Object> writeScopeClaim(ScopeClaim claim) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("level", claim.level().name());
        tree.put("id", claim.id());
        return tree;
    }

    private static Map<String, Object> writeConversation(Conversation conversation) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("id", conversation.id());
        tree.put("state", conversation.state().name());
        tree.put("principal", writePrincipal(conversation.principal()));
        tree.put("createdAt", conversation.createdAt().toString());
        // Optional.empty() -> JSON null, via the same Optional case in toTree.
        tree.put("closedAt", toTree(conversation.closedAt()));
        return tree;
    }

    private static Map<String, Object> writeCreateConversation(CreateConversation createConversation) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("principal", writePrincipal(createConversation.principal()));
        return tree;
    }

    private static Map<String, Object> writeErrorEnvelope(ErrorEnvelope errorEnvelope) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("code", errorEnvelope.code());
        tree.put("message", errorEnvelope.message());
        return tree;
    }

    private static Map<String, Object> writeConversationCreated(ConversationCreated created) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("conversationId", created.conversationId());
        tree.put("occurredAt", created.occurredAt().toString());
        tree.put("actor", writePrincipal(created.actor()));
        // The discriminator that lets a future read() know which AuditEvent
        // subtype this is - see AuditEventTypes.
        tree.put("type", AuditEventTypes.CONVERSATION_CREATED);
        return tree;
    }

    private static Map<String, Object> writeConversationClosed(ConversationClosed closed) {
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("conversationId", closed.conversationId());
        tree.put("occurredAt", closed.occurredAt().toString());
        tree.put("actor", writePrincipal(closed.actor()));
        tree.put("type", AuditEventTypes.CONVERSATION_CLOSED);
        return tree;
    }

    // ------------------------------------------------------------------
    // Reading: generic tree -> protocol type
    // ------------------------------------------------------------------

    /**
     * Maps a parsed JSON tree onto the requested {@code type}. One {@code
     * if} per supported type, mirroring the {@code switch} in {@link
     * #toTree(Object)}; {@code originalJson} is threaded through only for
     * {@link AuditEvent}/{@link UnknownAuditEvent}, which need the raw
     * source text to populate {@code rawJson()}.
     */
    private static Object fromTree(Object node, Class<?> type, String originalJson) {
        if (type == Principal.class) {
            return mapPrincipal(asObject(node, "root"));
        }
        if (type == ScopeClaim.class) {
            return mapScopeClaim(asObject(node, "root"));
        }
        if (type == ScopeLevel.class) {
            return mapEnum(ScopeLevel.class, asString(node, "root"));
        }
        if (type == ConversationState.class) {
            return mapEnum(ConversationState.class, asString(node, "root"));
        }
        if (type == Conversation.class) {
            return mapConversation(asObject(node, "root"));
        }
        if (type == CreateConversation.class) {
            return mapCreateConversation(asObject(node, "root"));
        }
        if (type == ErrorEnvelope.class) {
            return mapErrorEnvelope(asObject(node, "root"));
        }
        if (type == AuditEvent.class) {
            return mapAuditEvent(asObject(node, "root"), originalJson);
        }
        if (type == ConversationCreated.class) {
            return mapConversationCreated(asObject(node, "root"));
        }
        if (type == ConversationClosed.class) {
            return mapConversationClosed(asObject(node, "root"));
        }
        if (type == UnknownAuditEvent.class) {
            String discriminator = asString(field(asObject(node, "root"), "type"), "type");
            return mapUnknownAuditEvent(asObject(node, "root"), discriminator, originalJson);
        }
        throw new JsonException("Cannot read JSON as unsupported type: " + type.getName());
    }

    private static Principal mapPrincipal(Map<String, Object> obj) {
        String subject = asString(field(obj, "subject"), "subject");
        List<Object> claimsNode = asArray(field(obj, "claims"), "claims");
        List<ScopeClaim> claims =
                claimsNode.stream().map(node -> mapScopeClaim(asObject(node, "claims[]"))).toList();
        return new Principal(subject, claims);
    }

    private static ScopeClaim mapScopeClaim(Map<String, Object> obj) {
        ScopeLevel level = mapEnum(ScopeLevel.class, asString(field(obj, "level"), "level"));
        String id = asString(field(obj, "id"), "id");
        return new ScopeClaim(level, id);
    }

    private static Conversation mapConversation(Map<String, Object> obj) {
        String id = asString(field(obj, "id"), "id");
        ConversationState state = mapEnum(ConversationState.class, asString(field(obj, "state"), "state"));
        Principal principal = mapPrincipal(asObject(field(obj, "principal"), "principal"));
        Instant createdAt = mapInstant(asString(field(obj, "createdAt"), "createdAt"));
        Optional<Instant> closedAt = mapOptionalInstant(field(obj, "closedAt"));
        return new Conversation(id, state, principal, createdAt, closedAt);
    }

    private static CreateConversation mapCreateConversation(Map<String, Object> obj) {
        Principal principal = mapPrincipal(asObject(field(obj, "principal"), "principal"));
        return new CreateConversation(principal);
    }

    private static ErrorEnvelope mapErrorEnvelope(Map<String, Object> obj) {
        String code = asString(field(obj, "code"), "code");
        String message = asString(field(obj, "message"), "message");
        return new ErrorEnvelope(code, message);
    }

    private static ConversationCreated mapConversationCreated(Map<String, Object> obj) {
        return new ConversationCreated(
                asString(field(obj, "conversationId"), "conversationId"),
                mapInstant(asString(field(obj, "occurredAt"), "occurredAt")),
                mapPrincipal(asObject(field(obj, "actor"), "actor")));
    }

    private static ConversationClosed mapConversationClosed(Map<String, Object> obj) {
        return new ConversationClosed(
                asString(field(obj, "conversationId"), "conversationId"),
                mapInstant(asString(field(obj, "occurredAt"), "occurredAt")),
                mapPrincipal(asObject(field(obj, "actor"), "actor")));
    }

    /**
     * Dispatches a parsed {@link AuditEvent} object on its {@code type}
     * discriminator: a recognised value maps to the matching record, and -
     * this is the fallback behaviour deferred from issue #4 - an
     * unrecognised value maps to {@link UnknownAuditEvent} instead of
     * throwing. See {@link AuditEventTypes} for the (assumed) discriminator
     * strings.
     */
    private static AuditEvent mapAuditEvent(Map<String, Object> obj, String originalJson) {
        String discriminator = asString(field(obj, "type"), "type");
        return switch (discriminator) {
            case AuditEventTypes.CONVERSATION_CREATED -> mapConversationCreated(obj);
            case AuditEventTypes.CONVERSATION_CLOSED -> mapConversationClosed(obj);
            default -> mapUnknownAuditEvent(obj, discriminator, originalJson);
        };
    }

    private static UnknownAuditEvent mapUnknownAuditEvent(
            Map<String, Object> obj, String discriminator, String originalJson) {
        String conversationId = asString(field(obj, "conversationId"), "conversationId");
        Instant occurredAt = mapInstant(asString(field(obj, "occurredAt"), "occurredAt"));
        Principal actor = mapPrincipal(asObject(field(obj, "actor"), "actor"));
        // originalJson is the exact text passed to read(), preserved
        // verbatim as rawJson so nothing about the unrecognised event is
        // lost and write() can later re-emit it byte-for-byte.
        return new UnknownAuditEvent(conversationId, occurredAt, actor, discriminator, originalJson);
    }

    private static Instant mapInstant(String text) {
        try {
            return Instant.parse(text);
        } catch (DateTimeParseException e) {
            throw new JsonException("Invalid ISO-8601 instant: \"" + text + "\"", e);
        }
    }

    private static Optional<Instant> mapOptionalInstant(Object node) {
        // A null/absent closedAt (Map.get() returns null either way) means
        // "not closed yet" - matches Conversation's own normalization of a
        // null closedAt to Optional.empty() in its compact constructor.
        if (node == null) {
            return Optional.empty();
        }
        return Optional.of(mapInstant(asString(node, "closedAt")));
    }

    /**
     * Looks up an enum constant by exact {@code name()} match.
     *
     * <p>Deliberately strict: an unrecognised value throws rather than
     * falling back to some default. Being lenient here - e.g. mapping an
     * unknown value to some placeholder - is a possible future enhancement
     * (see issue #2), but for now only {@link AuditEvent}'s {@code type}
     * discriminator gets an "unknown value" fallback ({@link
     * UnknownAuditEvent}); every other enum in this SDK is closed and any
     * value outside it is treated as a genuine error.
     */
    private static <E extends Enum<E>> E mapEnum(Class<E> enumType, String name) {
        try {
            return Enum.valueOf(enumType, name);
        } catch (IllegalArgumentException e) {
            throw new JsonException(
                    "Unrecognised " + enumType.getSimpleName() + " value: \"" + name + "\"", e);
        }
    }

    // ------------------------------------------------------------------
    // Small helpers for pulling typed values out of the generic tree,
    // each throwing a JsonException (naming the field) on a shape mismatch
    // instead of letting a raw ClassCastException leak out.
    // ------------------------------------------------------------------

    private static Object field(Map<String, Object> obj, String name) {
        return obj.get(name);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asObject(Object node, String field) {
        if (!(node instanceof Map<?, ?> map)) {
            throw new JsonException(
                    "Expected \"" + field + "\" to be a JSON object but was " + describe(node));
        }
        return (Map<String, Object>) map;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> asArray(Object node, String field) {
        if (!(node instanceof List<?> list)) {
            throw new JsonException(
                    "Expected \"" + field + "\" to be a JSON array but was " + describe(node));
        }
        return (List<Object>) list;
    }

    private static String asString(Object node, String field) {
        if (!(node instanceof String s)) {
            throw new JsonException(
                    "Expected \"" + field + "\" to be a JSON string but was " + describe(node));
        }
        return s;
    }

    private static String describe(Object node) {
        if (node == null) {
            return "null";
        }
        if (node instanceof Map) {
            return "an object";
        }
        if (node instanceof List) {
            return "an array";
        }
        if (node instanceof String) {
            return "a string";
        }
        if (node instanceof Boolean) {
            return "a boolean";
        }
        if (node instanceof Number) {
            return "a number";
        }
        return node.getClass().getName();
    }
}
