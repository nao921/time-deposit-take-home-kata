package org.ikigaidigital;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

public class NoInterestTimeDepositPlanTest {

    private static double updatedBalance(TimeDeposit deposit, TimeDepositPlan plan) {
        double unrounded = plan.interestUnrounded(deposit);
        double roundedInterest = new BigDecimal(unrounded).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return deposit.getBalance() + roundedInterest;
    }

    @Test
    void unknownPlanType_noInterest() {
        var plan = new TimeDepositPlanFactory().from("gold");
        var deposit = new TimeDeposit(1, "gold", 1200.00, 365);

        assertThat(updatedBalance(deposit, plan)).isEqualTo(1200.00);
    }
}

