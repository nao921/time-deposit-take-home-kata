package org.ikigaidigital;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

public class PremiumTimeDepositPlanTest {

    private static double updatedBalance(TimeDeposit deposit, TimeDepositPlan plan) {
        double unrounded = plan.interestUnrounded(deposit);
        double roundedInterest = new BigDecimal(unrounded).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return deposit.getBalance() + roundedInterest;
    }

    @Test
    void premium_exactlyDay30_noInterest() {
        var deposit = new TimeDeposit(1, "premium", 1200.00, 30);
        var plan = new PremiumTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(1200.00);
    }

    @Test
    void day45_noInterest() {
        // boundary: days <= 45 → zero interest
        var deposit = new TimeDeposit(2, "premium", 1200.00, 45);
        var plan = new PremiumTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(1200.00);
    }

    @Test
    void day31_noInterest() {
        // days <= 45 → zero interest
        var deposit = new TimeDeposit(3, "premium", 1200.00, 31);
        var plan = new PremiumTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(1200.00);
    }

    @Test
    void day46_appliesFivePercentAnnualMonthly() {
        // interest = 1200 * 0.05 / 12 = 5.00
        var deposit = new TimeDeposit(4, "premium", 1200.00, 46);
        var plan = new PremiumTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(1205.00);
    }

    @Test
    void day365_appliesInterest() {
        var deposit = new TimeDeposit(5, "premium", 1200.00, 365);
        var plan = new PremiumTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(1205.00);
    }

    @Test
    void day366_appliesInterest() {
        // premium has no upper day limit
        var deposit = new TimeDeposit(6, "premium", 1200.00, 366);
        var plan = new PremiumTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(1205.00);
    }

    @Test
    void interestRoundedHalfUp() {
        // interest = 100 * 0.05 / 12 = 0.4166... → rounds to 0.42
        var deposit = new TimeDeposit(7, "premium", 100.00, 46);
        var plan = new PremiumTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(100.42);
    }
}

