package org.ikigaidigital.domain;

public class PremiumTimeDepositPlan extends AbstractTimeDepositPlan {
    private final double interestRate;
    private final int interestStartDays;

    public PremiumTimeDepositPlan(int gracePeriodDays, int monthsInYear, double interestRate, int interestStartDays) {
        super(gracePeriodDays, monthsInYear);
        this.interestRate = interestRate;
        this.interestStartDays = interestStartDays;
    }

    @Override
    protected double interestAfterGrace(TimeDeposit deposit) {
        // Premium applies only when days > interestStartDays.
        if (deposit.getDays() <= interestStartDays) {
            return 0.0;
        }
        return deposit.getBalance() * interestRate / monthsInYear;
    }
}
