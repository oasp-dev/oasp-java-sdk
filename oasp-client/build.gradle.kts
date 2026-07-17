// oasp-client is the core SDK. It is a plain Java library (no application
// main class, no framework), so it uses the "java-library" plugin, which
// is the conventional plugin for a jar that other projects depend on.
import org.gradle.api.artifacts.component.ProjectComponentIdentifier

plugins {
    `java-library`
}

dependencies {
    // --------------------------------------------------------------------
    // HARD RULE: oasp-client must compile and run using ONLY the JDK.
    // Do not add anything here with `implementation(...)` or `api(...)`.
    // If you think you need a library, talk to the team first - the
    // `verifyZeroRuntimeDependencies` task below (and CI) will fail the
    // build if one sneaks in.
    // --------------------------------------------------------------------

    // Test code is allowed to use libraries, since none of it ships in the
    // published jar or ends up on a consumer's runtime classpath.
    testImplementation("org.junit.jupiter:junit-jupiter:5.14.3")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.wiremock:wiremock-standalone:3.13.2")

    // Gradle 9 no longer auto-provides the JUnit Platform launcher on the
    // test runtime classpath, so we declare it explicitly. Without this,
    // `gradle test` fails with "Failed to load JUnit Platform" the moment
    // any test exists. Version 1.14.3 aligns with JUnit Jupiter 5.14.3.
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.14.3")
}

tasks.test {
    useJUnitPlatform {
        // "loom" tags tests that need a running OASP server (the reference
        // server is called Loom - see issue #11). NOTE: this is the Loom
        // *server*, NOT Java's Project Loom / virtual threads.
        //
        // They're excluded from the default build so it stays green without
        // external infrastructure. Issue #11 will add a dedicated way to run
        // them (a separate CI job / task) against a real Loom.
        excludeTags("loom")
    }
}

// ----------------------------------------------------------------------------
// Zero-dependency guard
//
// oasp-client promises consumers that it adds nothing to their classpath
// beyond the JDK. That promise is easy to break by accident (an IDE
// "quick fix" adding a library, a copy-pasted dependencies block, etc.),
// so it's enforced here in the build itself rather than only in code
// review or CI - anyone running the build locally will see it fail too.
//
// How it works: `runtimeClasspath` is the configuration Gradle resolves to
// decide what a consumer of this jar would need at runtime. We resolve it
// and check that every artifact in it came from *this build*
// (a ProjectComponentIdentifier) rather than from an external repository
// (Maven Central, etc). oasp-client has no sub-projects of its own, so the
// only correct answer is an empty list.
// ----------------------------------------------------------------------------
val verifyZeroRuntimeDependencies = tasks.register("verifyZeroRuntimeDependencies") {
    description = "Fails the build if oasp-client has gained any external runtime dependency."
    group = "verification"

    val runtimeClasspath = configurations.runtimeClasspath.get()

    doLast {
        val externalArtifacts = runtimeClasspath.incoming.artifacts.artifacts
            .filter { it.id.componentIdentifier !is ProjectComponentIdentifier }

        if (externalArtifacts.isNotEmpty()) {
            val offenders = externalArtifacts.joinToString(separator = "\n") { "  - ${it.id.componentIdentifier.displayName}" }
            throw GradleException(
                "oasp-client must have ZERO external runtime dependencies, but found:\n" +
                    "$offenders\n" +
                    "Remove this dependency from oasp-client/build.gradle.kts, or move it to " +
                    "testImplementation if it is only needed by tests."
            )
        }
    }
}

// Make sure nobody can get a green build (`./gradlew build` or `./gradlew check`)
// while this guard is failing - `check` already runs before `build` completes.
tasks.check {
    dependsOn(verifyZeroRuntimeDependencies)
}
