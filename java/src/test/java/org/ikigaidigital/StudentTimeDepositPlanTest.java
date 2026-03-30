package org.ikigaidigital;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentTimeDepositPlanTest {

    private static double updatedBalance(TimeDeposit deposit, TimeDepositPlan plan) {
        double unrounded = plan.interestUnrounded(deposit);
        double roundedInterest = new BigDecimal(unrounded).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return deposit.getBalance() + roundedInterest;
    }

    @Test
    void student_exactlyDay30_noInterest() {
        var deposit = new TimeDeposit(1, "student", 1200.00, 30);
        var plan = new StudentTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(1200.00);
    }

    @Test
    void day31_appliesThreePercentAnnualMonthly() {
        // interest = 1200 * 0.03 / 12 = 3.00
        var deposit = new TimeDeposit(2, "student", 1200.00, 31);
        var plan = new StudentTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(1203.00);
    }

    @Test
    void day365_appliesInterest() {
        // last day before the upper cutoff (<= 365 gets interest)
        var deposit = new TimeDeposit(3, "student", 1200.00, 365);
        var plan = new StudentTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(1203.00);
    }

    @Test
    void day366_noInterest() {
        // days >= 366 → zero interest for student
        var deposit = new TimeDeposit(4, "student", 1200.00, 366);
        var plan = new StudentTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(1200.00);
    }

    @Test
    void day400_noInterest() {
        var deposit = new TimeDeposit(5, "student", 1200.00, 400);
        var plan = new StudentTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(1200.00);
    }

    @Test
    void interestRoundedHalfUp() {
        // interest = 100 * 0.03 / 12 = 0.25
        var deposit = new TimeDeposit(6, "student", 100.00, 31);
        var plan = new StudentTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(100.25);
    }
}

