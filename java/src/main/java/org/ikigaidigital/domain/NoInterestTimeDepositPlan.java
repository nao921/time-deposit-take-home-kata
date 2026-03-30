package org.ikigaidigital.domain;

class NoInterestTimeDepositPlan implements TimeDepositPlan {
    @Override
    public double interestUnrounded(TimeDeposit deposit) {
        return 0.0;
    }
}
