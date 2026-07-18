package dev.oasp.client.http;

import dev.oasp.client.error.OaspTransportException;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;

/**
 * Sends a request with the one retry rule the protocol distinguishes: retry
 * <em>exactly once</em> on a connect-level failure (no answer at all), and
 * <em>never</em> on a delivered 4xx/5xx - a server that refused is an answer,
 * not an outage, so that's the caller's (mapping) concern, not a retry. An
 * interrupt re-sets the flag and is never retried.
 */
final class RetryingSender {

    private RetryingSender() {}

    static <T> HttpResponse<T> send(HttpClient client, HttpRequest request, BodyHandler<T> handler) {
        try {
            return client.send(request, handler);
        } catch (IOException firstFailure) {
            return retryOnce(client, request, handler, firstFailure);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OaspTransportException("Interrupted while awaiting OASP response", e);
        }
    }

    private static <T> HttpResponse<T> retryOnce(
            HttpClient client, HttpRequest request, BodyHandler<T> handler, IOException firstFailure) {
        try {
            return client.send(request, handler);
        } catch (IOException retryFailure) {
            var failure = new OaspTransportException("OASP request failed after one retry", retryFailure);
            failure.addSuppressed(firstFailure);
            throw failure;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OaspTransportException("Interrupted while awaiting OASP response", e);
        }
    }
}
