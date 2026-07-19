package dev.oasp.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the sample app.
 *
 * <p>{@code @SpringBootApplication} switches on Spring Boot's component
 * scanning and auto-configuration. Because {@code oasp-spring-boot-starter}
 * is on the classpath, its {@code OaspAutoConfiguration} runs during startup
 * and registers a ready-to-use {@link dev.oasp.client.OaspClient} bean built
 * from the {@code oasp.*} properties in {@code application.yaml}.
 *
 * <p>The app starts without contacting any OASP server: the client is only
 * used when the demo endpoint is called, so it boots cleanly with no Loom
 * running.
 */
@SpringBootApplication
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
