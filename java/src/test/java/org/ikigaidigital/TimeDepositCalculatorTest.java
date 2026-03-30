package org.ikigaidigital;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeDepositCalculatorTest {

    private TimeDepositCalculator calc;

    @BeforeEach
    void setUp() {
        calc = new TimeDepositCalculator();
    }

    @Nested
    class ZeroInterestBoundary {

        @Test
        void basic_exactlyDay30_noInterest() {
            var deposit = new TimeDeposit(1, "basic", 1200.00, 30);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(1200.00);
        }

        @Test
        void student_exactlyDay30_noInterest() {
            var deposit = new TimeDeposit(2, "student", 1200.00, 30);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(1200.00);
        }

        @Test
        void premium_exactlyDay30_noInterest() {
            var deposit = new TimeDeposit(3, "premium", 1200.00, 30);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(1200.00);
        }

        @Test
        void basic_day1_noInterest() {
            var deposit = new TimeDeposit(4, "basic", 500.00, 1);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(500.00);
        }
    }

    @Nested
    class BasicPlan {

        @Test
        void day31_appliesOnePercentAnnualMonthly() {
            // interest = 1200 * 0.01 / 12 = 1.00
            var deposit = new TimeDeposit(1, "basic", 1200.00, 31);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(1201.00);
        }

        @Test
        void day365_appliesInterest() {
            // interest = 1200 * 0.01 / 12 = 1.00
            var deposit = new TimeDeposit(2, "basic", 1200.00, 365);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(1201.00);
        }

        @Test
        void day366_appliesInterest() {
            // basic has no upper day limit — interest still applies
            var deposit = new TimeDeposit(3, "basic", 1200.00, 366);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(1201.00);
        }

        @Test
        void interestRoundedHalfUp() {
            // interest = 100 * 0.01 / 12 = 0.0833... → rounds to 0.08
            var deposit = new TimeDeposit(4, "basic", 100.00, 31);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(100.08);
        }
    }

    @Nested
    class StudentPlan {

        @Test
        void day31_appliesThreePercentAnnualMonthly() {
            // interest = 1200 * 0.03 / 12 = 3.00
            var deposit = new TimeDeposit(1, "student", 1200.00, 31);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(1203.00);
        }

        @Test
        void day365_appliesInterest() {
            // last day before the upper cutoff
            var deposit = new TimeDeposit(2, "student", 1200.00, 365);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(1203.00);
        }

        @Test
        void day366_noInterest() {
            // days >= 366 → zero interest for student
            var deposit = new TimeDeposit(3, "student", 1200.00, 366);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(1200.00);
        }

        @Test
        void day400_noInterest() {
            var deposit = new TimeDeposit(4, "student", 1200.00, 400);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(1200.00);
        }

        @Test
        void interestRoundedHalfUp() {
            // interest = 100 * 0.03 / 12 = 0.25
            var deposit = new TimeDeposit(5, "student", 100.00, 31);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(100.25);
        }
    }

    @Nested
    class PremiumPlan {

        @Test
        void day45_noInterest() {
            // boundary: days <= 45 → zero interest
            var deposit = new TimeDeposit(1, "premium", 1200.00, 45);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(1200.00);
        }

        @Test
        void day31_noInterest() {
            // days <= 30 already covered by global rule, but premium also requires > 45
            var deposit = new TimeDeposit(2, "premium", 1200.00, 31);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(1200.00);
        }

        @Test
        void day46_appliesFivePercentAnnualMonthly() {
            // interest = 1200 * 0.05 / 12 = 5.00
            var deposit = new TimeDeposit(3, "premium", 1200.00, 46);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(1205.00);
        }

        @Test
        void day365_appliesInterest() {
            var deposit = new TimeDeposit(4, "premium", 1200.00, 365);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(1205.00);
        }

        @Test
        void day366_appliesInterest() {
            // premium has no upper day limit
            var deposit = new TimeDeposit(5, "premium", 1200.00, 366);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(1205.00);
        }

        @Test
        void interestRoundedHalfUp() {
            // interest = 100 * 0.05 / 12 = 0.4166... → rounds to 0.42
            var deposit = new TimeDeposit(6, "premium", 100.00, 46);
            calc.updateBalance(List.of(deposit));
            assertThat(deposit.getBalance()).isEqualTo(100.42);
        }
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
