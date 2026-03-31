package org.ikigaidigital.infrastructure.adapter.in.rest.controller;

import org.ikigaidigital.application.model.TimeDepositView;
import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCasePort;
import org.ikigaidigital.application.port.in.UpdateBalancesUseCasePort;
import org.ikigaidigital.domain.withdrawal.Withdrawal;
import org.ikigaidigital.infrastructure.rest.generated.dto.TimeDepositResponse;
import org.ikigaidigital.infrastructure.rest.generated.dto.WithdrawalResponse;
import org.ikigaidigital.infrastructure.rest.generated.api.TimeDepositsApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller implementation for time deposit operations.
 * Implements the auto-generated TimeDepositsApi interface.
 * This is a driving adapter (calls the application layer).
 */
@RestController
public class TimeDepositController implements TimeDepositsApi {
    private final GetAllTimeDepositsUseCasePort getAllTimeDepositsUseCasePort;
    private final UpdateBalancesUseCasePort updateBalancesUseCasePort;

    public TimeDepositController(GetAllTimeDepositsUseCasePort getAllTimeDepositsUseCasePort,
                                 UpdateBalancesUseCasePort updateBalancesUseCasePort) {
        this.getAllTimeDepositsUseCasePort = getAllTimeDepositsUseCasePort;
        this.updateBalancesUseCasePort = updateBalancesUseCasePort;
    }

    /**
     * GET /time-deposits
     * Returns all time deposits with their withdrawals.
     */
    @Override
    public ResponseEntity<List<TimeDepositResponse>> getAllTimeDeposits() {
        List<TimeDepositResponse> response = getAllTimeDepositsUseCasePort.execute()
                .stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * POST /time-deposits/update-balances
     * Updates all time deposit balances based on accumulated interest.
     * Returns HTTP 200 with empty body.
     */
    @Override
    public ResponseEntity<Void> updateBalances() {
        updateBalancesUseCasePort.execute();
        return ResponseEntity.ok().build();
    }

    private TimeDepositResponse mapToResponse(TimeDepositView view) {
        List<WithdrawalResponse> withdrawalResponses = view.withdrawals()
                .stream()
                .map(this::mapToWithdrawalResponse)
                .toList();

        return new TimeDepositResponse(
                view.deposit().getId(),
                view.deposit().getPlanType(),
                view.deposit().getBalance(),
                view.deposit().getDays(),
                withdrawalResponses
        );
    }

    private WithdrawalResponse mapToWithdrawalResponse(Withdrawal withdrawal) {
        return new WithdrawalResponse(
                withdrawal.id(),
                withdrawal.timeDepositId(),
                withdrawal.amount(),
                withdrawal.date()
        );
    }
}
