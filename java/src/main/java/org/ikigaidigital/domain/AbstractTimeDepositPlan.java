package org.ikigaidigital.domain;

/**
 * Shared "grace period" logic:
 * - No interest for deposits during grace period
 * - Plan-specific logic applies only after the grace period
 */
public abstract class AbstractTimeDepositPlan implements TimeDepositPlan {

    protected final int gracePeriodDays;
    protected final int monthsInYear;

    protected AbstractTimeDepositPlan(int gracePeriodDays, int monthsInYear) {
        this.gracePeriodDays = gracePeriodDays;
        this.monthsInYear = monthsInYear;
    }

    @Override
    public final double interestUnrounded(TimeDeposit deposit) {
        if (deposit.getDays() <= gracePeriodDays) {
            return 0.0;
        }
        return interestAfterGrace(deposit);
    }

    protected abstract double interestAfterGrace(TimeDeposit deposit);
}
