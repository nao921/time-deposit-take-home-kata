package org.ikigaidigital.application.port;

import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.application.model.TimeDepositView;

import java.util.List;

/**
 * Port interface for time deposit repository.
 * Allows reading and persisting time deposit data.
 */
public interface TimeDepositRepository {
    /**
     * Retrieve all time deposits without their associated withdrawals.
     */
    List<TimeDeposit> findAllDeposits();

    /**
     * Retrieve all time deposits with their associated withdrawals.
     */
    List<TimeDepositView> findAllWithWithdrawals();

    /**
     * Persist/update the given time deposits (typically called after balance updates).
     */
    void saveAll(List<TimeDeposit> deposits);
}
