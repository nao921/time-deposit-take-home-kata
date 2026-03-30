package org.ikigaidigital.application.usecase;

import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.TimeDepositCalculator;
import org.ikigaidigital.application.port.TimeDepositRepository;

import java.util.List;

/**
 * Use case for updating time deposit balances based on accumulated interest.
 */
public class UpdateBalancesUseCase {
    private final TimeDepositRepository repository;
    private final TimeDepositCalculator calculator;

    public UpdateBalancesUseCase(TimeDepositRepository repository, TimeDepositCalculator calculator) {
        this.repository = repository;
        this.calculator = calculator;
    }

    /**
     * Execute the use case:
     * 1. Load all deposits
     * 2. Calculate interest and update balances
     * 3. Persist updated deposits
     */
    public void execute() {
        List<TimeDeposit> deposits = repository.findAllDeposits();
        calculator.updateBalance(deposits);
        repository.saveAll(deposits);
    }
}
