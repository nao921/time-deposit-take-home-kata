package org.ikigaidigital.application.usecase;

import org.ikigaidigital.application.model.TimeDepositView;
import org.ikigaidigital.application.port.TimeDepositRepository;
import org.ikigaidigital.domain.BasicTimeDepositPlan;
import org.ikigaidigital.domain.PremiumTimeDepositPlan;
import org.ikigaidigital.domain.StudentTimeDepositPlan;
import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.TimeDepositCalculator;
import org.ikigaidigital.domain.TimeDepositPlanFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateBalancesUseCaseTest {

    private static final int GRACE_PERIOD_DAYS = 30;
    private static final int MONTHS_IN_YEAR = 12;
    private static final double BASIC_RATE = 0.01;
    private static final double STUDENT_RATE = 0.03;
    private static final int STUDENT_MAX_DAYS = 366;
    private static final double PREMIUM_RATE = 0.05;
    private static final int PREMIUM_START_DAYS = 45;

    private TimeDepositCalculator createCalculator() {
        BasicTimeDepositPlan basicPlan = new BasicTimeDepositPlan(GRACE_PERIOD_DAYS, MONTHS_IN_YEAR, BASIC_RATE);
        StudentTimeDepositPlan studentPlan = new StudentTimeDepositPlan(GRACE_PERIOD_DAYS, MONTHS_IN_YEAR, STUDENT_RATE, STUDENT_MAX_DAYS);
        PremiumTimeDepositPlan premiumPlan = new PremiumTimeDepositPlan(GRACE_PERIOD_DAYS, MONTHS_IN_YEAR, PREMIUM_RATE, PREMIUM_START_DAYS);
        TimeDepositPlanFactory factory = new TimeDepositPlanFactory(basicPlan, studentPlan, premiumPlan);
        return new TimeDepositCalculator(factory);
    }

    @Test
    void execute_updatesBalancesAndPersists() {
        // Arrange
        SpyTimeDepositRepository repository = new SpyTimeDepositRepository();
        TimeDepositCalculator calculator = createCalculator();
        UpdateBalancesUseCase useCase = new UpdateBalancesUseCase(repository, calculator);

        // Act
        useCase.execute();

        // Assert
        assertThat(repository.savedDeposits).hasSize(2);
        assertThat(repository.savedDeposits.get(0).getBalance()).isEqualTo(1201.00);
        assertThat(repository.savedDeposits.get(1).getBalance()).isEqualTo(5020.83);
    }

    /**
     * Spy repository that tracks saved deposits.
     */
    static class SpyTimeDepositRepository implements TimeDepositRepository {
        List<TimeDeposit> savedDeposits = new ArrayList<>();

        @Override
        public List<TimeDeposit> findAllDeposits() {
            return List.of(
                    new TimeDeposit(1, "basic", 1200.00, 31),
                    new TimeDeposit(2, "premium", 5000.00, 46)
            );
        }

        @Override
        public List<TimeDepositView> findAllWithWithdrawals() {
            return List.of();
        }

        @Override
        public void saveAll(List<TimeDeposit> deposits) {
            savedDeposits = new ArrayList<>(deposits);
        }
    }
}
