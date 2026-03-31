package org.ikigaidigital.application.port.out;

import org.ikigaidigital.domain.timedeposit.TimeDeposit;
import org.ikigaidigital.application.model.TimeDepositView;

import java.util.List;

/**
 * Outbound port interface for time deposit repository.
 * Allows reading and persisting time deposit data.
 * Implemented by driven adapters (e.g., JPA, external APIs).
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
