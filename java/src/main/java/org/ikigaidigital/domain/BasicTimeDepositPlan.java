package org.ikigaidigital.domain;

public class BasicTimeDepositPlan extends AbstractTimeDepositPlan {
    private final double interestRate;

    public BasicTimeDepositPlan(int gracePeriodDays, int monthsInYear, double interestRate) {
        super(gracePeriodDays, monthsInYear);
        this.interestRate = interestRate;
    }

    @Override
    protected double interestAfterGrace(TimeDeposit deposit) {
        // Basic applies without an upper day limit.
        return deposit.getBalance() * interestRate / monthsInYear;
    }
}
