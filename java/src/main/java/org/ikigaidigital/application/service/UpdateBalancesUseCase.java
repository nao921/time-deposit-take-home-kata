package org.ikigaidigital.application.service;

import org.ikigaidigital.domain.timedeposit.TimeDeposit;
import org.ikigaidigital.domain.timedeposit.TimeDepositCalculator;
import org.ikigaidigital.application.port.in.UpdateBalancesUseCasePort;
import org.ikigaidigital.application.port.out.TimeDepositRepository;

import java.util.List;

/**
 * Service implementation for updating time deposit balances based on accumulated interest.
 * Implements the inbound port interface.
 */
public class UpdateBalancesUseCase implements UpdateBalancesUseCasePort {
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
    @Override
    public void execute() {
        List<TimeDeposit> deposits = repository.findAllDeposits();
        calculator.updateBalance(deposits);
        repository.saveAll(deposits);
    }
}
