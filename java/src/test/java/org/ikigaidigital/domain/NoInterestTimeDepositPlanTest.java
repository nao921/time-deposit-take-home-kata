package org.ikigaidigital.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

public class NoInterestTimeDepositPlanTest {

    private static double updatedBalance(TimeDeposit deposit, TimeDepositPlan plan) {
        double unrounded = plan.interestUnrounded(deposit);
        double roundedInterest = new BigDecimal(unrounded).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return deposit.getBalance() + roundedInterest;
    }

    @ParameterizedTest
    @CsvSource({
        // unknownPlanType_noInterest
        "1, gold, 1200.00, 365, 1200.00",
        // empty planType
        "3, '', 1000.00, 30, 1000.00"
    })
    void unknownPlanType_noInterest(int id, String planType, double balance, int days, double expectedBalance) {
        var plan = new TimeDepositPlanFactory().from(planType);
        var deposit = new TimeDeposit(id, planType, balance, days);

        assertThat(updatedBalance(deposit, plan)).isEqualTo(expectedBalance);
    }
}
