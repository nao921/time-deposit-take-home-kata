package org.ikigaidigital;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TimeDepositCalculator {
    private final TimeDepositPlanFactory planFactory = new TimeDepositPlanFactory();

    public void updateBalance(List<TimeDeposit> xs) {
        for (TimeDeposit deposit : xs) {
            double interest = planFactory.from(deposit.getPlanType()).interestUnrounded(deposit);

            double a2d = deposit.getBalance()
                    + (new BigDecimal(interest).setScale(2, RoundingMode.HALF_UP)).doubleValue();
            deposit.setBalance(a2d);
        }
    }
}
