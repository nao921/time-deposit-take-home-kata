package org.ikigaidigital.domain.timedeposit.plan;

import org.ikigaidigital.domain.timedeposit.TimeDeposit;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

public class NoInterestTimeDepositPlanTest {

    private static final int GRACE_PERIOD_DAYS = 30;
    private static final int MONTHS_IN_YEAR = 12;
    private static final double BASIC_RATE = 0.01;
    private static final double STUDENT_RATE = 0.03;
    private static final int STUDENT_MAX_DAYS = 366;
    private static final double PREMIUM_RATE = 0.05;
    private static final int PREMIUM_START_DAYS = 45;

    private static double updatedBalance(TimeDeposit deposit, TimeDepositPlan plan) {
        double unrounded = plan.interestUnrounded(deposit);
        double roundedInterest = new BigDecimal(unrounded).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return deposit.getBalance() + roundedInterest;
    }

    private TimeDepositPlanFactory createFactory() {
        BasicTimeDepositPlan basicPlan = new BasicTimeDepositPlan(GRACE_PERIOD_DAYS, MONTHS_IN_YEAR, BASIC_RATE);
        StudentTimeDepositPlan studentPlan = new StudentTimeDepositPlan(GRACE_PERIOD_DAYS, MONTHS_IN_YEAR, STUDENT_RATE, STUDENT_MAX_DAYS);
        PremiumTimeDepositPlan premiumPlan = new PremiumTimeDepositPlan(GRACE_PERIOD_DAYS, MONTHS_IN_YEAR, PREMIUM_RATE, PREMIUM_START_DAYS);
        return new TimeDepositPlanFactory(basicPlan, studentPlan, premiumPlan);
    }

    @ParameterizedTest
    @CsvSource({
        // unknownPlanType_noInterest
        "1, gold, 1200.00, 365, 1200.00",
        // empty planType
        "3, '', 1000.00, 30, 1000.00"
    })
    void unknownPlanType_noInterest(int id, String planType, double balance, int days, double expectedBalance) {
        var plan = createFactory().from(planType);
        var deposit = new TimeDeposit(id, planType, balance, days);

        assertThat(updatedBalance(deposit, plan)).isEqualTo(expectedBalance);
    }
}
