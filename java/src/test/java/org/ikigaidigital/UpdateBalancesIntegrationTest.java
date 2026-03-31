package org.ikigaidigital;

import org.ikigaidigital.domain.timedeposit.TimeDeposit;
import org.ikigaidigital.domain.timedeposit.TimeDepositCalculator;
import org.ikigaidigital.infrastructure.adapter.out.persistence.jpa.TimeDepositEntity;
import org.ikigaidigital.infrastructure.adapter.out.persistence.jpa.TimeDepositJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for POST /time-deposits/update-balances endpoint.
 * Verifies that balances are updated in the database using the same logic
 * as TimeDepositCalculator.updateBalance.
 */
public class UpdateBalancesIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TimeDepositJpaRepository timeDepositRepository;

    @Autowired
    private TimeDepositCalculator calculator;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @BeforeEach
    void setUp() {
        timeDepositRepository.deleteAll();
    }

    @Test
    void testUpdateBalances_PersistsCalculatorResultToDatabase() {
        // Arrange: seed deposits covering different plan types and day ranges
        timeDepositRepository.saveAll(List.of(
            new TimeDepositEntity(1, "basic",   new BigDecimal("1000.00"), 60),
            new TimeDepositEntity(2, "student", new BigDecimal("2000.00"), 180),
            new TimeDepositEntity(3, "premium", new BigDecimal("5000.00"), 90)
        ));

        // Compute expected balances using the calculator directly
        List<TimeDeposit> expected = List.of(
            new TimeDeposit(1, "basic",   1000.00, 60),
            new TimeDeposit(2, "student", 2000.00, 180),
            new TimeDeposit(3, "premium", 5000.00, 90)
        );
        calculator.updateBalance(expected);

        // Act: call POST /time-deposits/update-balances
        ResponseEntity<Void> response = restTemplate.postForEntity(
            getBaseUrl() + "/time-deposits/update-balances", null, Void.class
        );

        // Assert: HTTP 200
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Assert: each balance in DB matches the calculator output
        List<TimeDepositEntity> updated = timeDepositRepository.findAll();
        assertThat(updated).hasSize(3);

        for (TimeDeposit exp : expected) {
            TimeDepositEntity actual = updated.stream()
                .filter(e -> e.getId() == exp.getId())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Deposit not found: " + exp.getId()));

            assertThat(actual.getBalance().doubleValue())
                .as("balance for deposit id=%d planType=%s days=%d",
                    exp.getId(), exp.getPlanType(), exp.getDays())
                .isEqualTo(exp.getBalance());
        }
    }

    @Test
    void testUpdateBalances_ZeroInterestConditions_BalanceUnchanged() {
        // Arrange: deposits that should receive zero interest
        timeDepositRepository.saveAll(List.of(
            new TimeDepositEntity(10, "basic",   new BigDecimal("1000.00"), 30),  // days <= 30
            new TimeDepositEntity(11, "student", new BigDecimal("2000.00"), 366), // student days >= 366
            new TimeDepositEntity(12, "premium", new BigDecimal("3000.00"), 45)   // premium days <= 45
        ));

        // Act
        ResponseEntity<Void> response = restTemplate.postForEntity(
            getBaseUrl() + "/time-deposits/update-balances", null, Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Assert: balances are unchanged
        List<TimeDepositEntity> updated = timeDepositRepository.findAll();
        assertThat(updated).hasSize(3);

        assertThat(findById(updated, 10).getBalance()).isEqualByComparingTo("1000.00");
        assertThat(findById(updated, 11).getBalance()).isEqualByComparingTo("2000.00");
        assertThat(findById(updated, 12).getBalance()).isEqualByComparingTo("3000.00");
    }

    @Test
    void testUpdateBalances_EmptyRepository_ReturnsOk() {
        // Act: call with no deposits seeded
        ResponseEntity<Void> response = restTemplate.postForEntity(
            getBaseUrl() + "/time-deposits/update-balances", null, Void.class
        );

        // Assert: HTTP 200, no-op
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(timeDepositRepository.findAll()).isEmpty();
    }

    private TimeDepositEntity findById(List<TimeDepositEntity> entities, int id) {
        return entities.stream()
            .filter(e -> e.getId() == id)
            .findFirst()
            .orElseThrow(() -> new AssertionError("Deposit not found: " + id));
    }
}
