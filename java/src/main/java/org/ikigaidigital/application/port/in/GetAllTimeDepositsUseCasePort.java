package org.ikigaidigital.application.port.in;

import org.ikigaidigital.application.model.TimeDepositView;

/**
 * Inbound port for retrieving all time deposits.
 * Implemented by use cases, called by driving adapters.
 */
public interface GetAllTimeDepositsUseCasePort {
    /**
     * Execute the use case: retrieve all time deposits with withdrawals.
     *
     * @return list of time deposit views with associated withdrawals
     */
    java.util.List<TimeDepositView> execute();
}
