# Spring Boot live-streaming sample

A runnable Spring Boot app that shows the OASP Java SDK behind a **live-streaming
single-page app**. You type a message and the assistant's reply streams into a
chat bubble **token-by-token, as it arrives** — the live effect — over
[Server-Sent Events (SSE)](https://developer.mozilla.org/docs/Web/API/Server-sent_events).

The frontend is **dependency-free vanilla JavaScript**: plain static files
under [`src/main/resources/static`](src/main/resources/static) with **no Node,
npm, framework, or bundler** and no build step. The whole sample builds with
Gradle alone. (A production app would likely use React/Vue with a bundler; this
one stays self-contained on purpose.)

## What it demonstrates

The app depends on `oasp-spring-boot-starter`. On startup the starter's
`OaspAutoConfiguration` reads the `oasp.*` properties from
[`application.yaml`](src/main/resources/application.yaml) and registers a
`dev.oasp.client.OaspClient` bean. `ConverseController` receives that bean by
**constructor injection** — it never builds a client itself.

The streaming bridge, front to back:

1. `GET /` serves the static [`index.html`](src/main/resources/static/index.html)
   SPA (input bar + chat transcript).
2. On submit, [`app.js`](src/main/resources/static/app.js) opens an
   `EventSource` to `GET /api/converse?message=...`.
3. `ConverseController` returns an `SseEmitter` and runs the turn on a
   background thread ([`ConversationStream`](src/main/java/dev/oasp/sample/streaming/ConversationStream.java)):
   `conversations().create(...)` → `sessions().send(currentSessionId, ...)` →
   `sessions().stream(...)`, closing the `Stream<Event>` with try-with-resources.
4. Each OASP `Event` is flattened into a tiny JSON
   [`EventPayload`](src/main/java/dev/oasp/sample/streaming/EventPayload.java)
   (`{ kind, text?, status?, messageId? }`) and pushed onto the emitter.
5. The browser renders each payload live: `text` chunks are appended to the
   active assistant bubble; a final named `done` event tells the client to
   close the `EventSource` (so SSE does not auto-reconnect) before the server
   completes the emitter.

## Demo mode toggle

`oasp.demo` (in `application.yaml`) controls whether the SDK is really called:

- **`oasp.demo: true`** (the default here) — the endpoint does **not** call the
  client. It streams a scripted sequence of events (a message start, several
  assistant text chunks with a short delay between them, a message end, then an
  idle status), so the live token-by-token UI is demonstrable with **no Loom
  running and no network**.
- **`oasp.demo: false`** — the endpoint drives the real injected `OaspClient`
  against `oasp.base-url`, forwarding events as the server emits them. This
  needs a reachable OASP server (Loom) and a valid `OASP_TOKEN`.

## Run it

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

gradle :samples:spring-boot-streaming-ui:bootRun
```

Then open <http://localhost:8080> and type a message. In the default demo mode
it works immediately with no server. If you run the Thymeleaf sample at the
same time, give one a different port (e.g. `--server.port=8081`) to avoid a
clash.

To drive a real server, set `oasp.demo=false` and point it at your own Loom:

```bash
OASP_TOKEN=your-token \
  gradle :samples:spring-boot-streaming-ui:bootRun \
  --args='--oasp.demo=false --oasp.base-url=https://your-loom:8443'
```
