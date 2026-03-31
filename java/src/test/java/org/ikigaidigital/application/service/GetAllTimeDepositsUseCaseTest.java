package org.ikigaidigital.application.service;

import org.ikigaidigital.application.model.TimeDepositView;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.timedeposit.TimeDeposit;
import org.ikigaidigital.domain.withdrawal.Withdrawal;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetAllTimeDepositsUseCaseTest {

    @Test
    void execute_returnsAllDepositsWithWithdrawals() {
        // Arrange
        TimeDepositRepository repository = new StubTimeDepositRepository();
        GetAllTimeDepositsUseCase useCase = new GetAllTimeDepositsUseCase(repository);

        // Act
        List<TimeDepositView> result = useCase.execute();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).deposit().getId()).isEqualTo(1);
        assertThat(result.get(0).withdrawals()).hasSize(1);
        assertThat(result.get(1).deposit().getId()).isEqualTo(2);
        assertThat(result.get(1).withdrawals()).isEmpty();
    }

    @Test
    void execute_returnsEmptyListWhenNoDeposits() {
        // Arrange
        TimeDepositRepository repository = new StubTimeDepositRepository(true);
        GetAllTimeDepositsUseCase useCase = new GetAllTimeDepositsUseCase(repository);

        // Act
        List<TimeDepositView> result = useCase.execute();

        // Assert
        assertThat(result).isEmpty();
    }

    /**
     * Stub repository for testing.
     */
    static class StubTimeDepositRepository implements TimeDepositRepository {
        private final boolean empty;

        StubTimeDepositRepository() {
            this.empty = false;
        }

        StubTimeDepositRepository(boolean empty) {
            this.empty = empty;
        }

        @Override
        public List<TimeDeposit> findAllDeposits() {
            if (empty) return List.of();
            return List.of(
                    new TimeDeposit(1, "basic", 1200.00, 31),
                    new TimeDeposit(2, "premium", 5000.00, 60)
            );
        }

        @Override
        public List<TimeDepositView> findAllWithWithdrawals() {
            if (empty) return List.of();
            return List.of(
                    new TimeDepositView(
                            new TimeDeposit(1, "basic", 1200.00, 31),
                            List.of(new Withdrawal(101, 1, 100.00, LocalDate.of(2026, 1, 15)))
                    ),
                    new TimeDepositView(
                            new TimeDeposit(2, "premium", 5000.00, 60),
                            List.of()
                    )
            );
        }

        @Override
        public void saveAll(List<TimeDeposit> deposits) {
        }
    }
}
