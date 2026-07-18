package dev.oasp.client.http;

import dev.oasp.client.error.OaspTransportException;
import dev.oasp.client.json.JsonCodec;
import dev.oasp.client.types.Event;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Pulls one SSE frame at a time from the reader. A frame is the text up to the
 * next blank line; its {@code data:} lines (concatenated with newlines, per the
 * SSE spec) are the JSON of one {@link Event}. Comment lines ({@code :}...) and
 * non-{@code data} fields ({@code event:}, {@code id:}) are ignored - this SDK
 * derives ordering from each Event's own {@code id}.
 */
final class SseFrameIterator implements Iterator<Event> {

    private final BufferedReader reader;
    private final JsonCodec codec;
    private Event next;
    private boolean drained;

    SseFrameIterator(BufferedReader reader, JsonCodec codec) {
        this.reader = reader;
        this.codec = codec;
    }

    @Override
    public boolean hasNext() {
        if (next == null && !drained) {
            next = readFrame();
            drained = next == null;
        }
        return next != null;
    }

    @Override
    public Event next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Event frame = next;
        next = null;
        return frame;
    }

    private Event readFrame() {
        try {
            var data = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    if (data.length() > 0) {
                        return codec.read(data.toString(), Event.class);
                    }
                    continue; // keep-alive blank line between frames
                }
                appendData(data, line);
            }
            // Stream ended; flush a final frame not followed by a blank line.
            return data.length() > 0 ? codec.read(data.toString(), Event.class) : null;
        } catch (IOException e) {
            throw new OaspTransportException("Failed reading OASP event stream", e);
        }
    }

    private static void appendData(StringBuilder data, String line) {
        if (line.startsWith(":") || !line.startsWith("data:")) {
            return; // SSE comment, or a field we don't need (event:/id:/retry:)
        }
        String value = line.substring("data:".length());
        if (value.startsWith(" ")) {
            value = value.substring(1); // a single leading space after the colon is stripped
        }
        if (data.length() > 0) {
            data.append('\n');
        }
        data.append(value);
    }
}
