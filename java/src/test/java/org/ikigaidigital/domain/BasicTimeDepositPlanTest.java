package org.ikigaidigital.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicTimeDepositPlanTest {

    private static double updatedBalance(TimeDeposit deposit, TimeDepositPlan plan) {
        double unrounded = plan.interestUnrounded(deposit);
        double roundedInterest = new BigDecimal(unrounded).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return deposit.getBalance() + roundedInterest;
    }

    private static final int GRACE_PERIOD_DAYS = 30;
    private static final int MONTHS_IN_YEAR = 12;
    private static final double INTEREST_RATE = 0.01;

    @ParameterizedTest
    @CsvSource({
        // basic_exactlyDay30_noInterest
        "1, 1200.00, 30, 1200.00",
        // basic_day1_noInterest
        "2, 500.00, 1, 500.00",
        // day31_appliesOnePercentAnnualMonthly (interest = 1200 * 0.01 / 12 = 1.00)
        "3, 1200.00, 31, 1201.00",
        // day365_appliesInterest (interest = 1200 * 0.01 / 12 = 1.00)
        "4, 1200.00, 365, 1201.00",
        // day366_appliesInterest (basic has no upper day limit — interest still applies)
        "5, 1200.00, 366, 1201.00",
        // interestRoundedHalfUp (interest = 100 * 0.01 / 12 = 0.0833... → rounds to 0.08)
        "6, 100.00, 31, 100.08",
    })
    void basic_timeDepositPlans(int id, double balance, int days, double expectedBalance) {
        var deposit = new TimeDeposit(id, "basic", balance, days);
        var plan = new BasicTimeDepositPlan(GRACE_PERIOD_DAYS, MONTHS_IN_YEAR, INTEREST_RATE);

        assertThat(updatedBalance(deposit, plan)).isEqualTo(expectedBalance);
    }
}
