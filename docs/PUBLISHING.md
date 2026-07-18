# Publishing to Maven Central

Both modules publish to Maven Central under the verified `dev.oasp` namespace:

| Artifact | Coordinate |
|----------|-----------|
| Client | `dev.oasp:oasp-client:<version>` (zero dependencies) |
| Spring starter | `dev.oasp:oasp-spring-boot-starter:<version>` |

The version lives in the root [`build.gradle.kts`](../build.gradle.kts)
(`allprojects { version = … }`). Publishing config is the
`com.vanniktech.maven.publish` plugin in each module's `build.gradle.kts`
(sources + javadoc jars and a GPG signature are produced automatically).

## One-time setup (credentials)

Generate these and add them as **repository secrets** (Settings → Secrets and
variables → Actions). The values never appear in the repo or the build files —
Gradle reads them from the environment.

| Secret | What | How |
|--------|------|-----|
| `MAVEN_CENTRAL_USERNAME` / `MAVEN_CENTRAL_PASSWORD` | Central Portal publishing token | central.sonatype.com → Account → **Generate User Token** |
| `SIGNING_KEY` | ASCII-armored GPG **private** key | `gpg --export-secret-keys --armor <KEYID>` |
| `SIGNING_KEY_PASSWORD` | that key's passphrase | (whatever you set on `gpg --full-generate-key`) |

The GPG **public** key must be on a keyserver so Central can verify signatures:
`gpg --keyserver keyserver.ubuntu.com --send-keys <FINGERPRINT>`.

## Releasing

1. Bump `version` in the root `build.gradle.kts`, commit, and tag it:
   ```bash
   git tag v0.1.0-alpha.0 && git push origin v0.1.0-alpha.0
   ```
2. The [`release`](../.github/workflows/release.yml) workflow runs on the tag
   (or run it manually from the Actions tab) and uploads to the Central Portal.
3. With `publishToMavenCentral` (the default here) the deployment lands as
   **validated/pending** — finish it from the Portal UI. Switch the workflow to
   `publishAndReleaseToMavenCentral` once you're comfortable auto-releasing.

## Local publish / dry run

To try it locally, put the same four values in `~/.gradle/gradle.properties`
(`mavenCentralUsername`, `mavenCentralPassword`, `signingInMemoryKey`,
`signingInMemoryKeyPassword`) and run `gradle publishToMavenLocal` to stage the
artifacts into your local `~/.m2` without touching Central.
