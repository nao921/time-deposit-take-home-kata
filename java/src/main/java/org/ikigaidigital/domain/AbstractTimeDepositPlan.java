package org.ikigaidigital.domain;

/**
 * Shared "grace period" logic:
 * - No interest for deposits with days <= 30
 * - Plan-specific logic applies only after the grace period
 */
public abstract class AbstractTimeDepositPlan implements TimeDepositPlan {

    @Override
    public final double interestUnrounded(TimeDeposit deposit) {
        if (deposit.getDays() <= 30) {
            return 0.0;
        }
        return interestAfterGrace(deposit);
    }

    protected abstract double interestAfterGrace(TimeDeposit deposit);
}
