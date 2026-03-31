package org.ikigaidigital.domain.timedeposit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.ikigaidigital.domain.timedeposit.plan.TimeDepositPlanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeDepositCalculator {
    private static final Logger logger = LoggerFactory.getLogger(TimeDepositCalculator.class);
    private final TimeDepositPlanFactory planFactory;

    public TimeDepositCalculator(TimeDepositPlanFactory planFactory) {
        this.planFactory = planFactory;
    }

    public void updateBalance(List<TimeDeposit> xs) {
        for (TimeDeposit deposit : xs) {
            double balanceBefore = deposit.getBalance();

            double interest = planFactory.from(deposit.getPlanType()).interestUnrounded(deposit);

            double interestRounded = (new BigDecimal(interest).setScale(2, RoundingMode.HALF_UP)).doubleValue();
            double balanceAfter = balanceBefore + interestRounded;
            deposit.setBalance(balanceAfter);

            logger.info("Deposit id={}, planType={}, days={} - balance before: {}, balance after: {}, interest added: {}",
                    deposit.getId(), deposit.getPlanType(), deposit.getDays(), balanceBefore, balanceAfter, interestRounded);
        }
    }
}
