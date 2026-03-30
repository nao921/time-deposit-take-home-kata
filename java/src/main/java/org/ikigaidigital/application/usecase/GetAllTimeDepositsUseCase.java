package org.ikigaidigital.application.usecase;

import org.ikigaidigital.application.model.TimeDepositView;
import org.ikigaidigital.application.port.TimeDepositRepository;

import java.util.List;

/**
 * Use case for retrieving all time deposits with their withdrawals.
 */
public class GetAllTimeDepositsUseCase {
    private final TimeDepositRepository repository;

    public GetAllTimeDepositsUseCase(TimeDepositRepository repository) {
        this.repository = repository;
    }

    /**
     * Execute the use case: fetch all time deposits with their associated withdrawals.
     */
    public List<TimeDepositView> execute() {
        return repository.findAllWithWithdrawals();
    }
}
