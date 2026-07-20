// A runnable Spring Boot app that shows the OASP SDK behind a *live-streaming*
// browser UI: a dependency-free vanilla-JS single-page app that opens a
// Server-Sent Events (SSE) connection and renders each assistant text chunk
// as it arrives — the token-by-token "live" effect.
//
// Like the other samples it consumes the auto-configured OaspClient bean from
// the starter. There is deliberately NO Node/npm/bundler step: the frontend is
// plain static files under src/main/resources/static, so the whole sample is
// self-contained and builds with Gradle alone.
//
// It applies the `org.springframework.boot` plugin so `bootRun` / a bootable
// jar work. Being a sample that is never published, it deliberately does NOT
// apply `com.vanniktech.maven.publish`.
plugins {
    java
    id("org.springframework.boot") version "3.5.16"
    id("io.spring.dependency-management") version "1.1.6"
}

dependencies {
    // Pulls in oasp-client AND the auto-configuration that builds the client
    // bean for us, so the controller can just inject an OaspClient.
    implementation(project(":oasp-spring-boot-starter"))

    // Embedded web server + Spring MVC. Provides @RestController and, crucially
    // here, SseEmitter for streaming Server-Sent Events to the browser.
    implementation("org.springframework.boot:spring-boot-starter-web")

    // @SpringBootTest support for the context-loads test.
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Gradle 9 no longer puts the JUnit Platform launcher on the test runtime
    // classpath automatically, so declare it. No version: Spring Boot's
    // dependency management pins the launcher to match the JUnit Platform it
    // ships, so the two never drift out of sync.
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
