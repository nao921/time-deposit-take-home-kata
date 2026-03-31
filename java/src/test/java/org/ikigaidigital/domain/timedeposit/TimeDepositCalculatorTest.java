package org.ikigaidigital.domain.timedeposit;

import org.ikigaidigital.domain.timedeposit.plan.BasicTimeDepositPlan;
import org.ikigaidigital.domain.timedeposit.plan.PremiumTimeDepositPlan;
import org.ikigaidigital.domain.timedeposit.plan.StudentTimeDepositPlan;
import org.ikigaidigital.domain.timedeposit.plan.TimeDepositPlanFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeDepositCalculatorTest {

    private static final int GRACE_PERIOD_DAYS = 30;
    private static final int MONTHS_IN_YEAR = 12;
    private static final double BASIC_RATE = 0.01;
    private static final double STUDENT_RATE = 0.03;
    private static final int STUDENT_MAX_DAYS = 366;
    private static final double PREMIUM_RATE = 0.05;
    private static final int PREMIUM_START_DAYS = 45;

    private TimeDepositCalculator calc;

    @BeforeEach
    void setUp() {
        BasicTimeDepositPlan basicPlan = new BasicTimeDepositPlan(GRACE_PERIOD_DAYS, MONTHS_IN_YEAR, BASIC_RATE);
        StudentTimeDepositPlan studentPlan = new StudentTimeDepositPlan(GRACE_PERIOD_DAYS, MONTHS_IN_YEAR, STUDENT_RATE, STUDENT_MAX_DAYS);
        PremiumTimeDepositPlan premiumPlan = new PremiumTimeDepositPlan(GRACE_PERIOD_DAYS, MONTHS_IN_YEAR, PREMIUM_RATE, PREMIUM_START_DAYS);
        TimeDepositPlanFactory factory = new TimeDepositPlanFactory(basicPlan, studentPlan, premiumPlan);
        calc = new TimeDepositCalculator(factory);
    }

    @Nested
    class MultipleDeposits {

        @Test
        void updatesAllDepositsIndependently() {
            var basic   = new TimeDeposit(1, "basic",   1200.00, 31);  // +1.00
            var student = new TimeDeposit(2, "student", 1200.00, 31);  // +3.00
            var premium = new TimeDeposit(3, "premium", 1200.00, 46);  // +5.00
            var noInt   = new TimeDeposit(4, "basic",   1200.00, 30);  // +0.00

            calc.updateBalance(List.of(basic, student, premium, noInt));

            assertThat(basic.getBalance()).isEqualTo(1201.00);
            assertThat(student.getBalance()).isEqualTo(1203.00);
            assertThat(premium.getBalance()).isEqualTo(1205.00);
            assertThat(noInt.getBalance()).isEqualTo(1200.00);
        }

        @Test
        void emptyList_doesNotThrow() {
            calc.updateBalance(List.of());
            // no assertion needed — just must not throw
        }
    }

}
