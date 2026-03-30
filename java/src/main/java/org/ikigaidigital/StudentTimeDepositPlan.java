package org.ikigaidigital;

class StudentTimeDepositPlan extends AbstractTimeDepositPlan {
    @Override
    protected double interestAfterGrace(TimeDeposit deposit) {
        // Student has no interest for days >= 366 (monthly interest only applies for 31..365).
        if (deposit.getDays() >= 366) {
            return 0.0;
        }
        return deposit.getBalance() * 0.03 / 12;
    }
}

