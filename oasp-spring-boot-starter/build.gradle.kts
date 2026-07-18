// oasp-spring-boot-starter lets a Spring Boot application pull in
// oasp-client with Spring auto-configuration (so beans are created
// automatically from application.properties) instead of wiring the client
// up by hand.
//
// This module is a library that Spring Boot applications depend on, not an
// application itself, so it uses "java-library" - NOT the
// "org.springframework.boot" plugin. That plugin builds executable/bootable
// jars (with an embedded server, a repackaged fat jar, etc.), which only
// makes sense for the final application, not for a library like this one.
plugins {
    `java-library`
}

dependencies {
    // The client this starter is auto-configuring.
    api(project(":oasp-client"))

    // spring-boot-autoconfigure brings @AutoConfiguration, @ConditionalOn...
    // annotations and the auto-configuration registration mechanism, without
    // pulling in a full Spring Boot application (embedded server, etc).
    api("org.springframework.boot:spring-boot-autoconfigure:3.5.16")

    // Generates spring-configuration-metadata.json from our @ConfigurationProperties
    // classes, which is what gives IDEs autocomplete + descriptions for our
    // properties in application.properties/yml. It only runs at compile
    // time and never ships in the jar, so it's an annotationProcessor, not
    // a regular dependency.
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.5.16")

    // Test the auto-configuration in isolation with ApplicationContextRunner
    // (spring-boot-test) against real Spring contexts (spring-boot-autoconfigure
    // is already on the compile classpath via `api` above).
    testImplementation("org.springframework.boot:spring-boot-test:3.5.16")
    testImplementation("org.junit.jupiter:junit-jupiter:5.14.3")
    testImplementation("org.assertj:assertj-core:3.27.7")

    // Gradle 9 no longer auto-provides the JUnit Platform launcher on the
    // test runtime classpath, so we declare it explicitly (see oasp-client).
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.14.3")
}
