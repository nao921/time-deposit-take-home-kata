package org.ikigaidigital.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

public class PremiumTimeDepositPlanTest {

    private static double updatedBalance(TimeDeposit deposit, TimeDepositPlan plan) {
        double unrounded = plan.interestUnrounded(deposit);
        double roundedInterest = new BigDecimal(unrounded).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return deposit.getBalance() + roundedInterest;
    }

    @ParameterizedTest
    @CsvSource({
        // premium_exactlyDay30_noInterest
        "1, 1200.00, 30, 1200.00",
        // day45_noInterest (boundary: days <= 45 → zero interest)
        "2, 1200.00, 45, 1200.00",
        // day31_noInterest (days <= 45 → zero interest)
        "3, 1200.00, 31, 1200.00",
        // day46_appliesFivePercentAnnualMonthly (interest = 1200 * 0.05 / 12 = 5.00)
        "4, 1200.00, 46, 1205.00",
        // day365_appliesInterest
        "5, 1200.00, 365, 1205.00",
        // day366_appliesInterest (premium has no upper day limit)
        "6, 1200.00, 366, 1205.00",
        // interestRoundedHalfUp (interest = 100 * 0.05 / 12 = 0.4166... → rounds to 0.42)
        "7, 100.00, 46, 100.42",
    })
    void premium_timeDepositPlans(int id, double balance, int days, double expectedBalance) {
        var deposit = new TimeDeposit(id, "premium", balance, days);
        var plan = new PremiumTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(expectedBalance);
    }
}
