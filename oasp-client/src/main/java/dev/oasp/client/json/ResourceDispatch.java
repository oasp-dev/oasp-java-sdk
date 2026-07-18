package dev.oasp.client.json;

import dev.oasp.client.types.Resource;
import dev.oasp.client.types.UnknownResource;
import java.util.Map;

/**
 * Dispatches a parsed {@link Resource} on its {@code resourceType}
 * discriminator - the core of this package's forward-compatibility posture.
 * A recognised value maps to the matching record; an unrecognised one maps
 * to {@link UnknownResource} instead of throwing, so a server built against
 * a newer oasp-standard version than this SDK still deserializes instead of
 * failing outright. {@code originalJson} is the exact text passed to {@code
 * read()}, threaded through only to populate an {@link UnknownResource}'s
 * {@code rawJson} byte-for-byte (and to let a nested unrecognised {@code
 * Event} type do the same - see {@link EventReaders}).
 */
final class ResourceDispatch {

    private ResourceDispatch() {}

    static Resource mapResource(Map<String, Object> obj, String originalJson) {
        var resourceType = JsonFields.string(obj, "resourceType");
        return switch (resourceType) {
            case "Principal" -> TopLevelReaders.mapPrincipal(obj);
            case "Conversation" -> TopLevelReaders.mapConversation(obj);
            case "Session" -> TopLevelReaders.mapSession(obj);
            case "AuditEvent" -> AuditReaders.mapAuditEvent(obj);
            case "Event" -> EventReaders.mapEvent(obj, originalJson);
            default -> new UnknownResource(resourceType, originalJson);
        };
    }
}
