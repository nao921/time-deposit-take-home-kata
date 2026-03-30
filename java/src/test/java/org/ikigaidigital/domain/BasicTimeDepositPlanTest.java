package org.ikigaidigital.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicTimeDepositPlanTest {

    private static double updatedBalance(TimeDeposit deposit, TimeDepositPlan plan) {
        double unrounded = plan.interestUnrounded(deposit);
        double roundedInterest = new BigDecimal(unrounded).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return deposit.getBalance() + roundedInterest;
    }

    @Test
    void basic_exactlyDay30_noInterest() {
        var deposit = new TimeDeposit(1, "basic", 1200.00, 30);
        var plan = new BasicTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(1200.00);
    }

    @Test
    void basic_day1_noInterest() {
        var deposit = new TimeDeposit(2, "basic", 500.00, 1);
        var plan = new BasicTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(500.00);
    }

    @Test
    void day31_appliesOnePercentAnnualMonthly() {
        // interest = 1200 * 0.01 / 12 = 1.00
        var deposit = new TimeDeposit(3, "basic", 1200.00, 31);
        var plan = new BasicTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(1201.00);
    }

    @Test
    void day365_appliesInterest() {
        // interest = 1200 * 0.01 / 12 = 1.00
        var deposit = new TimeDeposit(4, "basic", 1200.00, 365);
        var plan = new BasicTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(1201.00);
    }

    @Test
    void day366_appliesInterest() {
        // basic has no upper day limit — interest still applies
        var deposit = new TimeDeposit(5, "basic", 1200.00, 366);
        var plan = new BasicTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(1201.00);
    }

    @Test
    void interestRoundedHalfUp() {
        // interest = 100 * 0.01 / 12 = 0.0833... → rounds to 0.08
        var deposit = new TimeDeposit(6, "basic", 100.00, 31);
        var plan = new BasicTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(100.08);
    }
}
