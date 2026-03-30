package org.ikigaidigital.domain;

class BasicTimeDepositPlan extends AbstractTimeDepositPlan {
    @Override
    protected double interestAfterGrace(TimeDeposit deposit) {
        // Basic applies without an upper day limit.
        return deposit.getBalance() * 0.01 / 12;
    }
}
