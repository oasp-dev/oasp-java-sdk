package dev.oasp.client.http;

import dev.oasp.client.json.JsonCodec;
import dev.oasp.client.json.JsonException;
import dev.oasp.client.types.ErrorEnvelope;

/**
 * Best-effort decoding of an error-response body into an {@link
 * ErrorEnvelope}. A server that answered with an error status may not have
 * sent a (parseable) envelope, so this never throws: an absent or malformed
 * body yields {@code null}, which {@link
 * dev.oasp.client.error.ProtocolErrors} already tolerates.
 */
final class Envelopes {

    private Envelopes() {}

    static ErrorEnvelope tryParse(String body, JsonCodec codec) {
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            return codec.read(body, ErrorEnvelope.class);
        } catch (JsonException e) {
            return null;
        }
    }
}
