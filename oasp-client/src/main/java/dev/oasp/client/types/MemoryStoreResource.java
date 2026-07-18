package dev.oasp.client.types;

import java.util.Objects;

/**
 * An opaque memory store mounted into a {@link Session} at create. Per
 * oasp-standard's v0 decisions, Memory is deliberately opaque in v0 - a
 * first-class Memory resource with defined internals is a v1 candidate.
 *
 * @param storeId opaque identifier of the mounted memory store
 */
public record MemoryStoreResource(String storeId) implements SessionResource {

    public MemoryStoreResource {
        Objects.requireNonNull(storeId, "storeId");
    }

    @Override
    public String type() {
        return "memory_store";
    }
}
