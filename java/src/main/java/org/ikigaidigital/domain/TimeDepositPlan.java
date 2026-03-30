package org.ikigaidigital.domain;

public interface TimeDepositPlan {
    /**
     * @return the unrounded monthly interest amount that should be applied to the deposit.
     */
    double interestUnrounded(TimeDeposit deposit);
}
