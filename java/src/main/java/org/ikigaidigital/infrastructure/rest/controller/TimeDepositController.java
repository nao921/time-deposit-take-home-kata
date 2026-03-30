package org.ikigaidigital.infrastructure.rest.controller;

import org.ikigaidigital.application.model.TimeDepositView;
import org.ikigaidigital.application.usecase.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.usecase.UpdateBalancesUseCase;
import org.ikigaidigital.domain.Withdrawal;
import org.ikigaidigital.infrastructure.rest.generated.dto.TimeDepositResponse;
import org.ikigaidigital.infrastructure.rest.generated.dto.WithdrawalResponse;
import org.ikigaidigital.infrastructure.rest.generated.api.TimeDepositsApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller implementation for time deposit operations.
 * Implements the auto-generated TimeDepositsApi interface.
 */
@RestController
public class TimeDepositController implements TimeDepositsApi {
    private final GetAllTimeDepositsUseCase getAllTimeDepositsUseCase;
    private final UpdateBalancesUseCase updateBalancesUseCase;

    public TimeDepositController(GetAllTimeDepositsUseCase getAllTimeDepositsUseCase,
                                 UpdateBalancesUseCase updateBalancesUseCase) {
        this.getAllTimeDepositsUseCase = getAllTimeDepositsUseCase;
        this.updateBalancesUseCase = updateBalancesUseCase;
    }

    /**
     * GET /time-deposits
     * Returns all time deposits with their withdrawals.
     */
    @Override
    public ResponseEntity<List<TimeDepositResponse>> getAllTimeDeposits() {
        List<TimeDepositResponse> response = getAllTimeDepositsUseCase.execute()
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
        updateBalancesUseCase.execute();
        return ResponseEntity.ok().build();
    }

    private TimeDepositResponse mapToResponse(TimeDepositView view) {
        List<WithdrawalResponse> withdrawalResponses = view.withdrawals()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return new TimeDepositResponse(
                view.deposit().getId(),
                view.deposit().getPlanType(),
                view.deposit().getBalance(),
                view.deposit().getDays(),
                withdrawalResponses
        );
    }

    private WithdrawalResponse mapToResponse(Withdrawal withdrawal) {
        return new WithdrawalResponse(
                withdrawal.id(),
                withdrawal.timeDepositId(),
                withdrawal.amount(),
                withdrawal.date()
        );
    }
}

