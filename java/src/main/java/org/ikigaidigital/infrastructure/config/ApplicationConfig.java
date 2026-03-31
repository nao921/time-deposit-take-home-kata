package org.ikigaidigital.infrastructure.config;

import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCasePort;
import org.ikigaidigital.application.port.in.UpdateBalancesUseCasePort;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.application.service.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.service.UpdateBalancesUseCase;
import org.ikigaidigital.domain.timedeposit.TimeDepositCalculator;
import org.ikigaidigital.domain.timedeposit.plan.BasicTimeDepositPlan;
import org.ikigaidigital.domain.timedeposit.plan.PremiumTimeDepositPlan;
import org.ikigaidigital.domain.timedeposit.plan.StudentTimeDepositPlan;
import org.ikigaidigital.domain.timedeposit.plan.TimeDepositPlanFactory;
import org.ikigaidigital.infrastructure.adapter.out.persistence.mapper.TimeDepositJpaMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Spring configuration for application dependencies.
 * Wires adapters to ports and creates domain beans.
 */
@Configuration
@EnableConfigurationProperties(TimeDepositPlanProperties.class)
public class ApplicationConfig {

    @Bean
    public TimeDepositJpaMapper timeDepositJpaMapper() {
        return new TimeDepositJpaMapper();
    }

    @Bean
    public TimeDepositCalculator timeDepositCalculator(TimeDepositPlanFactory planFactory) {
        return new TimeDepositCalculator(planFactory);
    }

    @Bean
    public GetAllTimeDepositsUseCasePort getAllTimeDepositsUseCasePort(TimeDepositRepository repository) {
        return new GetAllTimeDepositsUseCase(repository);
    }

    @Bean
    public UpdateBalancesUseCasePort updateBalancesUseCasePort(TimeDepositRepository repository,
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
