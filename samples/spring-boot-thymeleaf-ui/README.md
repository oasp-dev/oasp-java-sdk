# Spring Boot Thymeleaf sample

A runnable Spring Boot app that shows the OASP Java SDK behind a **classic
server-rendered UI** — plain HTML rendered with [Thymeleaf](https://www.thymeleaf.org/),
no JavaScript framework. You type a message, submit the form, the server runs
the conversation flow and returns a fully-rendered results page.

## What it demonstrates

The app depends on `oasp-spring-boot-starter`. On startup the starter's
`OaspAutoConfiguration` reads the `oasp.*` properties from
[`application.yaml`](src/main/resources/application.yaml) and registers a
`dev.oasp.client.OaspClient` bean. `ConverseController` receives that bean by
**constructor injection** — it never builds a client itself.

The flow is request → server-runs-flow → render (no live streaming; the
separate streaming sample shows events arriving live):

1. `GET /` renders [`index.html`](src/main/resources/templates/index.html) — a
   text input and a submit button.
2. `POST /converse` runs the flow and renders
   [`result.html`](src/main/resources/templates/result.html):
   `conversations().create(...)` → `sessions().send(currentSessionId, ...)` →
   `sessions().stream(...)`, collecting the first ~10 `Event`s into a list.

## Demo mode toggle

`oasp.demo` (in `application.yaml`) controls whether the SDK is really called:

- **`oasp.demo: true`** (the default here) — the controller does **not** call
  the client. It renders a scripted list of sample events (a few assistant
  text chunks, then an idle status) so the whole UI is demonstrable with **no
  Loom running and no network**.
- **`oasp.demo: false`** — the controller drives the real injected
  `OaspClient` against `oasp.base-url`. This needs a reachable OASP server
  (Loom) and a valid `OASP_TOKEN`.

## Run it

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

gradle :samples:spring-boot-thymeleaf-ui:bootRun
```

Then open <http://localhost:8080>, type a message, and submit. In the default
demo mode it works immediately with no server.

To drive a real server, set `oasp.demo=false` and point it at your own Loom:

```bash
OASP_TOKEN=your-token \
  gradle :samples:spring-boot-thymeleaf-ui:bootRun \
  --args='--oasp.demo=false --oasp.base-url=https://your-loom:8443'
```
