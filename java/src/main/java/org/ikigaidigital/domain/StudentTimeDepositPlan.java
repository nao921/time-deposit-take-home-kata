package org.ikigaidigital.domain;

public class StudentTimeDepositPlan extends AbstractTimeDepositPlan {
    private final double interestRate;
    private final int maxInterestDays;

    public StudentTimeDepositPlan(int gracePeriodDays, int monthsInYear, double interestRate, int maxInterestDays) {
        super(gracePeriodDays, monthsInYear);
        this.interestRate = interestRate;
        this.maxInterestDays = maxInterestDays;
    }

    @Override
    protected double interestAfterGrace(TimeDeposit deposit) {
        // Student has no interest for days > maxInterestDays (monthly interest only applies after grace period).
        if (deposit.getDays() > maxInterestDays) {
            return 0.0;
        }
        return deposit.getBalance() * interestRate / monthsInYear;
    }
}
