// A runnable Spring Boot application that shows how a real app consumes the
// OASP SDK: it depends on the starter, and Spring auto-configures an
// OaspClient bean from the `oasp.*` properties, which a controller injects.
//
// Unlike the library modules, this one applies the `org.springframework.boot`
// plugin so `bootRun` / a bootable jar work. Because it is a sample and never
// published, it deliberately does NOT apply `com.vanniktech.maven.publish`.
plugins {
    java
    id("org.springframework.boot") version "3.5.16"
    id("io.spring.dependency-management") version "1.1.6"
}

dependencies {
    // Pulls in oasp-client AND the auto-configuration that builds the client
    // bean for us. This single line is the whole point of the sample.
    implementation(project(":oasp-spring-boot-starter"))

    // Gives us an embedded web server and @RestController so we can expose the
    // demo endpoint that exercises the injected client.
    implementation("org.springframework.boot:spring-boot-starter-web")

    // @SpringBootTest support for the context-loads test.
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Gradle 9 no longer puts the JUnit Platform launcher on the test runtime
    // classpath automatically, so declare it. No version: Spring Boot's
    // dependency management pins the launcher to match the JUnit Platform it
    // ships, so it stays in lockstep with spring-boot-starter-test.
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
