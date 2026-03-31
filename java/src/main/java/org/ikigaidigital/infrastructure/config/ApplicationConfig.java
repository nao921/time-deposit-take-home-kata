package org.ikigaidigital.infrastructure.config;

import org.ikigaidigital.application.port.TimeDepositRepository;
import org.ikigaidigital.application.usecase.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.usecase.UpdateBalancesUseCase;
import org.ikigaidigital.domain.BasicTimeDepositPlan;
import org.ikigaidigital.domain.PremiumTimeDepositPlan;
import org.ikigaidigital.domain.StudentTimeDepositPlan;
import org.ikigaidigital.domain.TimeDepositCalculator;
import org.ikigaidigital.domain.TimeDepositPlanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Spring configuration for application dependencies.
 */
@Configuration
@EnableConfigurationProperties(TimeDepositPlanProperties.class)
public class ApplicationConfig {

    @Bean
    public TimeDepositCalculator timeDepositCalculator(TimeDepositPlanFactory planFactory) {
        return new TimeDepositCalculator(planFactory);
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

    @Bean
    public BasicTimeDepositPlan basicTimeDepositPlan(TimeDepositPlanProperties properties) {
        return new BasicTimeDepositPlan(
                properties.getGracePeriodDays(),
                properties.getMonthsInYear(),
                properties.getPlans().getBasic().getInterestRate()
        );
    }

    @Bean
    public StudentTimeDepositPlan studentTimeDepositPlan(TimeDepositPlanProperties properties) {
        return new StudentTimeDepositPlan(
                properties.getGracePeriodDays(),
                properties.getMonthsInYear(),
                properties.getPlans().getStudent().getInterestRate(),
                properties.getPlans().getStudent().getMaxInterestDays()
        );
    }

    @Bean
    public PremiumTimeDepositPlan premiumTimeDepositPlan(TimeDepositPlanProperties properties) {
        return new PremiumTimeDepositPlan(
                properties.getGracePeriodDays(),
                properties.getMonthsInYear(),
                properties.getPlans().getPremium().getInterestRate(),
                properties.getPlans().getPremium().getInterestStartDays()
        );
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
