package org.ikigaidigital.application.service;

import org.ikigaidigital.application.model.TimeDepositView;
import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCasePort;
import org.ikigaidigital.application.port.out.TimeDepositRepository;

import java.util.List;

/**
 * Service implementation for retrieving all time deposits with their withdrawals.
 * Implements the inbound port interface.
 */
public class GetAllTimeDepositsUseCase implements GetAllTimeDepositsUseCasePort {
    private final TimeDepositRepository repository;

    public GetAllTimeDepositsUseCase(TimeDepositRepository repository) {
        this.repository = repository;
    }

    /**
     * Execute the use case: fetch all time deposits with their associated withdrawals.
     */
    @Override
    public List<TimeDepositView> execute() {
        return repository.findAllWithWithdrawals();
    }
}
