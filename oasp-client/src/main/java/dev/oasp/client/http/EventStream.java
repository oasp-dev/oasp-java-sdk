package dev.oasp.client.http;

import dev.oasp.client.json.JsonCodec;
import dev.oasp.client.types.Event;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Turns a {@code text/event-stream} (Server-Sent Events) response body into a
 * lazy {@link Stream} of {@link Event}s - one per SSE frame, parsed by {@link
 * SseFrameIterator}. The stream is <em>closeable</em>: closing it (or
 * exhausting it in a try-with-resources) closes the underlying HTTP response
 * body, so a caller that stops reading early does not leak the connection.
 *
 * <p>Unknown event {@code type}s are not this class's concern: {@link
 * JsonCodec} already maps them to {@link dev.oasp.client.types.UnknownEvent}.
 */
final class EventStream {

    private EventStream() {}

    static Stream<Event> from(InputStream body, JsonCodec codec) {
        var reader = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8));
        var frames = new SseFrameIterator(reader, codec);
        Spliterator<Event> spliterator =
                Spliterators.spliteratorUnknownSize(frames, Spliterator.ORDERED | Spliterator.NONNULL);
        return StreamSupport.stream(spliterator, false).onClose(() -> close(reader));
    }

    private static void close(BufferedReader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
