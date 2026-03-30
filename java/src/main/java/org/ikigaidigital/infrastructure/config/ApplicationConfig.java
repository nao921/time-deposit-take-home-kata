package org.ikigaidigital.infrastructure.config;

import org.ikigaidigital.application.port.TimeDepositRepository;
import org.ikigaidigital.application.usecase.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.usecase.UpdateBalancesUseCase;
import org.ikigaidigital.domain.TimeDepositCalculator;
import org.ikigaidigital.infrastructure.persistence.InMemoryTimeDepositRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for application dependencies.
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public TimeDepositRepository timeDepositRepository() {
        return new InMemoryTimeDepositRepositoryAdapter();
    }

    @Bean
    public TimeDepositCalculator timeDepositCalculator() {
        return new TimeDepositCalculator();
    }

    @Bean
    public GetAllTimeDepositsUseCase getAllTimeDepositsUseCase(TimeDepositRepository repository) {
        return new GetAllTimeDepositsUseCase(repository);
    }

    @Bean
    public UpdateBalancesUseCase updateBalancesUseCase(TimeDepositRepository repository,
                                                        TimeDepositCalculator calculator) {
        return new UpdateBalancesUseCase(repository, calculator);
    }
}
