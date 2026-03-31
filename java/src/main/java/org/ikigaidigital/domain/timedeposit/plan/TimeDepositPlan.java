package org.ikigaidigital.domain.timedeposit.plan;

import org.ikigaidigital.domain.timedeposit.TimeDeposit;

public interface TimeDepositPlan {
    /**
     * @return the unrounded monthly interest amount that should be applied to the deposit.
     */
    double interestUnrounded(TimeDeposit deposit);
}
