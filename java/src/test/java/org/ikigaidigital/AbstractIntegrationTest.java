package org.ikigaidigital;

import java.time.Duration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:16-alpine")
    )
            .withStartupAttempts(1)
            .withStartupTimeout(Duration.ofSeconds(30));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // Optimize for faster test execution and cleaner shutdown
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> 2);
        registry.add("spring.datasource.hikari.minimum-idle", () -> 0);
        registry.add("spring.datasource.hikari.connection-timeout", () -> 10000);
        registry.add("spring.datasource.hikari.idle-timeout", () -> 30000);
        registry.add("spring.datasource.hikari.max-lifetime", () -> 60000);
        registry.add("spring.datasource.hikari.auto-commit", () -> true);
        registry.add("spring.datasource.hikari.validation-timeout", () -> 1000);
        // Disable leak detection in tests to avoid false positives
        registry.add("spring.datasource.hikari.leak-detection-threshold", () -> 0);
    }
}
