package dev.oasp.client.json;

import dev.oasp.client.types.FileResource;
import dev.oasp.client.types.GithubRepositoryResource;
import dev.oasp.client.types.MemoryStoreResource;
import dev.oasp.client.types.SessionResource;
import dev.oasp.client.types.UnknownSessionResource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Writes one {@link SessionResource} mount, dispatched on its concrete
 * variant. An {@link UnknownSessionResource} is re-parsed from its preserved
 * {@code rawJson} rather than rebuilt field-by-field, same as {@link
 * dev.oasp.client.types.UnknownResource}/{@code UnknownEvent} get one level
 * up in {@link HandRolledJsonCodec} - except this one is never itself the
 * top-level {@code write()} target, so there is no original document text to
 * fall back to for byte-for-byte fidelity; re-serializing its parsed tree is
 * the closest available approximation (same fields, same order, same
 * values).
 */
final class SessionResourceWriters {

    private SessionResourceWriters() {}

    static Map<String, Object> write(SessionResource resource) {
        return switch (resource) {
            case FileResource file -> {
                Map<String, Object> tree = new LinkedHashMap<>();
                tree.put("type", SessionResourceTypes.FILE);
                tree.put("fileId", file.fileId());
                yield tree;
            }
            case MemoryStoreResource memoryStore -> {
                Map<String, Object> tree = new LinkedHashMap<>();
                tree.put("type", SessionResourceTypes.MEMORY_STORE);
                tree.put("storeId", memoryStore.storeId());
                yield tree;
            }
            case GithubRepositoryResource repo -> {
                Map<String, Object> tree = new LinkedHashMap<>();
                tree.put("type", SessionResourceTypes.GITHUB_REPOSITORY);
                tree.put("owner", repo.owner());
                tree.put("repo", repo.repo());
                ProtocolTreeWriter.putIfPresent(tree, "ref", repo.ref());
                yield tree;
            }
            case UnknownSessionResource unknown -> JsonTrees.asObject(JsonParser.parse(unknown.rawJson()), "root");
        };
    }
}
