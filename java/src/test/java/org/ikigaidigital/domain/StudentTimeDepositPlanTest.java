package org.ikigaidigital.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentTimeDepositPlanTest {

    private static double updatedBalance(TimeDeposit deposit, TimeDepositPlan plan) {
        double unrounded = plan.interestUnrounded(deposit);
        double roundedInterest = new BigDecimal(unrounded).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return deposit.getBalance() + roundedInterest;
    }

    @ParameterizedTest
    @CsvSource({
        // student_exactlyDay30_noInterest
        "1, 1200.00, 30, 1200.00",
        // day31_appliesThreePercentAnnualMonthly (interest = 1200 * 0.03 / 12 = 3.00)
        "2, 1200.00, 31, 1203.00",
        // day365_appliesInterest (last day before the upper cutoff (<= 365 gets interest))
        "3, 1200.00, 365, 1203.00",
        // day366_noInterest (days >= 366 → zero interest for student)
        "4, 1200.00, 366, 1200.00",
        // day400_noInterest
        "5, 1200.00, 400, 1200.00",
        // interestRoundedHalfUp (interest = 100 * 0.03 / 12 = 0.25)
        "6, 100.00, 31, 100.25",
    })
    void student_timeDepositPlans(int id, double balance, int days, double expectedBalance) {
        var deposit = new TimeDeposit(id, "student", balance, days);
        var plan = new StudentTimeDepositPlan();

        assertThat(updatedBalance(deposit, plan)).isEqualTo(expectedBalance);
    }
}
