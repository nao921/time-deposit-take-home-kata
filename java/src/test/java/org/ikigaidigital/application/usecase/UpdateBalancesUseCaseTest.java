package org.ikigaidigital.application.usecase;

import org.ikigaidigital.application.model.TimeDepositView;
import org.ikigaidigital.application.port.TimeDepositRepository;
import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.TimeDepositCalculator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateBalancesUseCaseTest {

    @Test
    void execute_updatesBalancesAndPersists() {
        // Arrange
        SpyTimeDepositRepository repository = new SpyTimeDepositRepository();
        TimeDepositCalculator calculator = new TimeDepositCalculator();
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
