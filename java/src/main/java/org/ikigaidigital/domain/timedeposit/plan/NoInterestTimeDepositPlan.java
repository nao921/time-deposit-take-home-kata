package org.ikigaidigital.domain.timedeposit.plan;

import org.ikigaidigital.domain.timedeposit.TimeDeposit;

public class NoInterestTimeDepositPlan implements TimeDepositPlan {
    @Override
    public double interestUnrounded(TimeDeposit deposit) {
        return 0.0;
    }
}
