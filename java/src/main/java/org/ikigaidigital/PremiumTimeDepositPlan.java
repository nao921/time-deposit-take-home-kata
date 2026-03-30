package org.ikigaidigital;

class PremiumTimeDepositPlan extends AbstractTimeDepositPlan {
    @Override
    protected double interestAfterGrace(TimeDeposit deposit) {
        // Premium applies only when days > 45.
        if (deposit.getDays() <= 45) {
            return 0.0;
        }
        return deposit.getBalance() * 0.05 / 12;
    }
}

