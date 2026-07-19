# Spring Boot sample

A minimal, runnable Spring Boot app that shows how a real application consumes
the OASP Java SDK **through the Spring Boot starter**.

## What it demonstrates

The app depends only on `oasp-spring-boot-starter`. On startup the starter's
`OaspAutoConfiguration` reads the `oasp.*` properties from
[`application.yaml`](src/main/resources/application.yaml) and registers a
ready-to-use `dev.oasp.client.OaspClient` bean. `ConversationDemoController`
then receives that bean by **constructor injection** — it never builds a client
itself.

The `POST /demo/converse` endpoint runs the real end-to-end flow with the
injected client:

1. `conversations().create(...)` — mint a conversation (and its first session),
2. `sessions().send(sessionId, ...)` — post a message to
   `conversation.currentSessionId()`,
3. `sessions().stream(sessionId)` — read back the first few streamed `Event`s,
   returned as JSON.

The app **starts without contacting any server** — the client is only exercised
when the endpoint is hit.

## Run it

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

OASP_TOKEN=your-token gradle :samples:spring-boot-sample:bootRun
```

Then, in another terminal:

```bash
curl -X POST localhost:8080/demo/converse
```

The endpoint needs a reachable OASP server (Loom) at the configured
`oasp.base-url` to actually succeed. Without one the app still starts and
demonstrates the auto-configuration wiring; the request just fails when it tries
to reach the server.

Point it at your own server and token via `application.yaml` or environment
overrides (e.g. `oasp.base-url`, `OASP_TOKEN`).
