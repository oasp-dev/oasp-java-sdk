// Root build file. Nothing here builds a jar of its own — this project is
// just a container for the "oasp-client" and "oasp-spring-boot-starter"
// modules declared in settings.gradle.kts.
//
// The `subprojects { ... }` block below applies identical configuration to
// every module, so we don't have to repeat it in each module's build file.

subprojects {
    // Each module applies the "java-library" plugin itself (see
    // oasp-client/build.gradle.kts and oasp-spring-boot-starter/build.gradle.kts).
    // `withPlugin("java")` reacts once that happens, so this shared
    // configuration runs for every module without this root file having to
    // apply any plugin itself.
    pluginManager.withPlugin("java") {

        // Pin the language level and the JDK used to compile/run every
        // module to Java 21, regardless of which JDK started the Gradle
        // build itself. Gradle will auto-detect or download a matching
        // JDK if needed.
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }

        // All test code in this project is written with JUnit 5 (the
        // "Jupiter" API), so tell Gradle's test task to run tests through
        // the JUnit Platform rather than the legacy JUnit 4 runner.
        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }
}
