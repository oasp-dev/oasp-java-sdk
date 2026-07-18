# oasp-java-sdk

A Java client SDK for the [OASP](https://www.oasp.dev) protocol. `oasp-client`
carries **zero external dependencies beyond the JDK**.

## Quickstart

```java
OaspClient client = OaspClient.builder()
    .baseUrl(URI.create("https://loom.local:8443"))
    .tokenProvider(() -> tokenFromSomewhere())     // sent as Authorization: Bearer per request
    .build();

// Start a conversation, then talk to the agent over the session it rides on.
Conversation conv = client.conversations().create(new CreateConversation(
    new Scope(ScopeLevel.WORKSPACE, "ws-42"),
    new PrincipalRef(PrincipalKind.USER, "user-1"),
    "my-agent-definition",
    List.of()));

client.sessions().send(conv.currentSessionId(), "hello");
try (Stream<Event> events = client.sessions().stream(conv.currentSessionId())) {
    events.forEach(System.out::println);           // assistant_message_*, tool-use, status, …
}
```

A **Conversation** is a durable thread that rides disposable **Sessions**; you
address the conversation, and OASP routes to the active session. See
[`docs/spec`](https://github.com/oasp-dev/oasp-standard/tree/main/docs/spec) for
the model, and [`docs/JAVA-NOTES.md`](docs/JAVA-NOTES.md) for the design
decisions Java forced, and [`DECISIONS.md`](DECISIONS.md) for the recorded
architecture decisions.

> Some request bodies (`createConversation`, `send`) are provisional — the
> upstream OpenAPI marks them placeholders; they'll firm up as `oasp-standard`
> pins the wire shapes.

## Building

### Prerequisites

- **JDK 21** (Temurin recommended). Verify with `java -version`.
- **Gradle 9.6.1** — pinned; this repo does **not** commit a Gradle wrapper
  (no binaries in the repo), so you install Gradle yourself.

The version is pinned in [`.sdkmanrc`](.sdkmanrc). With
[SDKMAN](https://sdkman.io):

```bash
sdk env install   # installs the pinned Gradle 9.6.1 (one-time)
sdk env           # switch this directory to it (per shell)
```

Or install any way you like (e.g. `brew install gradle`), just match 9.6.1.
CI provisions the same version via the `gradle/actions/setup-gradle` action.

### Build & test

```bash
gradle build      # compiles both modules, runs tests, enforces the zero-dep rule
gradle test       # tests only (integration tests tagged @Tag("loom") are excluded)
```

`gradle build` fails if `oasp-client` ever gains an external runtime dependency
(see the `verifyZeroRuntimeDependencies` task in `oasp-client/build.gradle.kts`).

## Modules

| Module | Description |
|--------|-------------|
| `oasp-client` | The SDK. Plain Java library, zero runtime dependencies beyond the JDK. |
| `oasp-spring-boot-starter` | Spring Boot auto-configuration for `oasp-client`. |
