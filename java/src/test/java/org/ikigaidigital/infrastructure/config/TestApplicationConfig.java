package org.ikigaidigital.infrastructure.config;

import org.ikigaidigital.application.port.TimeDepositRepository;
import org.ikigaidigital.infrastructure.persistence.InMemoryTimeDepositRepositoryAdapter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration that provides in-memory repository for tests.
 */
@TestConfiguration
public class TestApplicationConfig {

    @Bean
    @Primary
    public TimeDepositRepository testTimeDepositRepository() {
        return new InMemoryTimeDepositRepositoryAdapter();
    }

    @Bean
    public InMemoryTimeDepositRepositoryAdapter inMemoryTimeDepositRepositoryAdapter() {
        return new InMemoryTimeDepositRepositoryAdapter();
    }
}
