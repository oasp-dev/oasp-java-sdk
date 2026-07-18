package dev.oasp.client.http;

import dev.oasp.client.error.ProtocolErrors;
import dev.oasp.client.json.JsonCodec;
import dev.oasp.client.types.Event;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.stream.Stream;

/**
 * The single point where this SDK talks to an OASP server over HTTP. It owns
 * request construction (base URL, per-request bearer token, timeouts), the
 * JSON (de)serialisation via {@link JsonCodec}, and the two failure rules
 * the protocol distinguishes: a server that <em>answered</em> with an error
 * status maps to a {@link dev.oasp.client.error.OaspException} via {@link
 * ProtocolErrors}; a server that never answered (connect/timeout) maps to
 * {@link OaspTransportException} and is retried exactly once.
 */
public final class OaspHttpTransport {

    private final HttpClient httpClient;
    private final URI baseUrl;
    private final TokenProvider tokenProvider;
    private final Duration requestTimeout;
    private final JsonCodec codec;

    public OaspHttpTransport(
            HttpClient httpClient, URI baseUrl, TokenProvider tokenProvider, Duration requestTimeout) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
        this.tokenProvider = tokenProvider;
        this.requestTimeout = requestTimeout;
        this.codec = JsonCodec.getDefault();
    }

    /** POST a JSON-serialised {@code body}, returning the response deserialised as {@code type}. */
    public <T> T post(String path, Object body, Class<T> type) {
        var request = request(path).POST(BodyPublishers.ofString(codec.write(body))).build();
        return codec.read(sendForBody(request), type);
    }

    /** POST with no request body (e.g. {@code drain}), returning the response deserialised as {@code type}. */
    public <T> T post(String path, Class<T> type) {
        var request = request(path).POST(BodyPublishers.noBody()).build();
        return codec.read(sendForBody(request), type);
    }

    /** POST a pre-serialised JSON {@code body} where the server answers {@code 202} with no body to read. */
    public void postAccepted(String path, String body) {
        var request = request(path).POST(BodyPublishers.ofString(body)).build();
        sendForBody(request);
    }

    /** Open {@code GET path} as a lazily-parsed, closeable stream of {@link Event}s (Server-Sent Events). */
    public Stream<Event> stream(String path) {
        var request = request(path).GET().build();
        HttpResponse<InputStream> response = RetryingSender.send(httpClient, request, BodyHandlers.ofInputStream());
        if (response.statusCode() / 100 != 2) {
            throw ProtocolErrors.fromResponse(response.statusCode(), Envelopes.tryParse(readAll(response.body()), codec));
        }
        return EventStream.from(response.body(), codec);
    }

    /** Executes a request expected to answer with a body, mapping a non-2xx answer to the matching exception. */
    private String sendForBody(HttpRequest request) {
        HttpResponse<String> response = RetryingSender.send(httpClient, request, BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            throw ProtocolErrors.fromResponse(response.statusCode(), Envelopes.tryParse(response.body(), codec));
        }
        return response.body();
    }

    /** A fresh request builder carrying the per-request bearer token, JSON content type, and request timeout. */
    private HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder(baseUrl.resolve(path))
                .timeout(requestTimeout)
                .header("Authorization", "Bearer " + tokenProvider.getToken())
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");
    }

    private static String readAll(InputStream body) {
        try (body) {
            return new String(body.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}
