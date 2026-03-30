package org.ikigaidigital.domain;

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
