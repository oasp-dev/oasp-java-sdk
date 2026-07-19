// Declares the modules that make up this build and the overall project name.
// Gradle needs this file to know which sub-projects exist before it can
// evaluate any of their build.gradle.kts files.
rootProject.name = "oasp-java-sdk"

include("oasp-client")
include("oasp-spring-boot-starter")

// A runnable Spring Boot app that demonstrates consuming the SDK through the
// starter's auto-configuration. It is a sample, not a published artifact.
include("samples:spring-boot-sample")

// Declare the dependency repositories once, here, instead of repeating
// `repositories { mavenCentral() }` in every module's build.gradle.kts.
// `FAIL_ON_PROJECT_REPOS` stops a module from silently adding its own
// extra repository, keeping the set of places we download code from in
// one auditable list.
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}
