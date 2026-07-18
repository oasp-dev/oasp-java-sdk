package dev.oasp.client.json;

import dev.oasp.client.types.FileResource;
import dev.oasp.client.types.GithubRepositoryResource;
import dev.oasp.client.types.MemoryStoreResource;
import dev.oasp.client.types.SessionResource;
import dev.oasp.client.types.UnknownSessionResource;
import java.util.Map;

/**
 * Dispatches a parsed mount on its {@code type} discriminator. An
 * unrecognised value maps to {@link UnknownSessionResource} instead of
 * throwing - the same forward-compatible fallback used one level up for
 * {@link dev.oasp.client.types.Resource}, applied to the embedded mount
 * union instead of the top-level resource discriminator.
 */
final class SessionResourceReaders {

    private SessionResourceReaders() {}

    static SessionResource mapSessionResource(Map<String, Object> obj) {
        var type = JsonFields.string(obj, "type");
        return switch (type) {
            case SessionResourceTypes.FILE -> new FileResource(JsonFields.string(obj, "fileId"));
            case SessionResourceTypes.MEMORY_STORE -> new MemoryStoreResource(JsonFields.string(obj, "storeId"));
            case SessionResourceTypes.GITHUB_REPOSITORY -> new GithubRepositoryResource(
                    JsonFields.string(obj, "owner"),
                    JsonFields.string(obj, "repo"),
                    JsonFields.optionalString(obj, "ref"));
            default -> new UnknownSessionResource(type, JsonWriter.write(obj));
        };
    }
}
