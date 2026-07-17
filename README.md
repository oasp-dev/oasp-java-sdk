# oasp-java-sdk

A Java client SDK for the OASP protocol. `oasp-client` carries **zero external
dependencies beyond the JDK**.

> This is a stub. The usage quickstart (builder + a lifecycle call) lands with
> issue #12; for now this covers only how to build the project.

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
