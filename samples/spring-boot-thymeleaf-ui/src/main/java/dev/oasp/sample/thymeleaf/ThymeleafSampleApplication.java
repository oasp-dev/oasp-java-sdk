package dev.oasp.sample.thymeleaf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the server-rendered (Thymeleaf) sample app.
 *
 * <p>{@code @SpringBootApplication} switches on Spring Boot's component
 * scanning and auto-configuration. Because {@code oasp-spring-boot-starter}
 * is on the classpath, its {@code OaspAutoConfiguration} runs during startup
 * and registers a ready-to-use {@link dev.oasp.client.OaspClient} bean built
 * from the {@code oasp.*} properties in {@code application.yaml}.
 *
 * <p>Run it, then open <a href="http://localhost:8080">http://localhost:8080</a>.
 */
@SpringBootApplication
public class ThymeleafSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThymeleafSampleApplication.class, args);
    }
}
