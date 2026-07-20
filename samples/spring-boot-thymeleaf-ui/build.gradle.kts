// A runnable Spring Boot app that shows the OASP SDK behind a classic
// server-rendered UI (Thymeleaf HTML templates, no JavaScript framework).
// Like the other samples it consumes the auto-configured OaspClient bean
// from the starter; unlike them it renders HTML pages instead of JSON.
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
    // bean for us, so a controller can just inject an OaspClient.
    implementation(project(":oasp-spring-boot-starter"))

    // Embedded web server + Spring MVC (@Controller, form binding).
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Thymeleaf template engine + its Spring integration. This is what turns
    // the HTML files under src/main/resources/templates into rendered pages.
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // @SpringBootTest support for the context-loads test.
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Gradle 9 no longer puts the JUnit Platform launcher on the test runtime
    // classpath automatically, so declare it. No version: Spring Boot's
    // dependency management pins the launcher to match the JUnit Platform it
    // ships, so the two never drift out of sync.
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
