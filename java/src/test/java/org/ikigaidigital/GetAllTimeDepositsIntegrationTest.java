package org.ikigaidigital;

import org.ikigaidigital.infrastructure.persistence.TimeDepositEntity;
import org.ikigaidigital.infrastructure.persistence.TimeDepositJpaRepository;
import org.ikigaidigital.infrastructure.persistence.WithdrawalEntity;
import org.ikigaidigital.infrastructure.persistence.WithdrawalJpaRepository;
import org.ikigaidigital.infrastructure.rest.generated.dto.TimeDepositResponse;
import org.ikigaidigital.infrastructure.rest.generated.dto.WithdrawalResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for GET /time-deposits endpoint.
 */
public class GetAllTimeDepositsIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TimeDepositJpaRepository timeDepositRepository;

    @Autowired
    private WithdrawalJpaRepository withdrawalRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @BeforeEach
    void setUp() {
        withdrawalRepository.deleteAll();
        timeDepositRepository.deleteAll();
    }

    @Test
    void testGetAllTimeDeposits_ReturnsSeededData() {
        // Arrange: Seed time deposits and withdrawals
        TimeDepositEntity deposit1 = new TimeDepositEntity(1, "basic", new BigDecimal("1000.00"), 45);
        timeDepositRepository.save(deposit1);

        WithdrawalEntity withdrawal1 = new WithdrawalEntity(1, 1, 100.0, LocalDate.of(2026, 1, 15));
        withdrawalRepository.save(withdrawal1);

        TimeDepositEntity deposit2 = new TimeDepositEntity(2, "premium", new BigDecimal("5000.50"), 180);
        timeDepositRepository.save(deposit2);

        WithdrawalEntity withdrawal2 = new WithdrawalEntity(2, 2, 250.75, LocalDate.of(2026, 2, 20));
        withdrawalRepository.save(withdrawal2);

        WithdrawalEntity withdrawal3 = new WithdrawalEntity(3, 2, 500.0, LocalDate.of(2026, 3, 10));
        withdrawalRepository.save(withdrawal3);

        // Act: Call GET /time-deposits
        ResponseEntity<TimeDepositResponse[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/time-deposits",
                TimeDepositResponse[].class
        );

        // Assert: HTTP 200
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Assert: Response body matches seeded data
        TimeDepositResponse[] deposits = response.getBody();
        assertThat(deposits).isNotNull().hasSize(2);

        // Check first deposit
        TimeDepositResponse depositResponse1 = deposits[0];
        assertThat(depositResponse1.getId()).isEqualTo(1);
        assertThat(depositResponse1.getPlanType()).isEqualTo("basic");
        assertThat(depositResponse1.getBalance()).isEqualByComparingTo(1000.00);
        assertThat(depositResponse1.getDays()).isEqualTo(45);
        assertThat(depositResponse1.getWithdrawals()).hasSize(1);

        WithdrawalResponse withdrawalResponse1 = depositResponse1.getWithdrawals().get(0);
        assertThat(withdrawalResponse1.getId()).isEqualTo(1);
        assertThat(withdrawalResponse1.getTimeDepositId()).isEqualTo(1);
        assertThat(withdrawalResponse1.getAmount()).isEqualTo(100.0);
        assertThat(withdrawalResponse1.getDate()).isEqualTo(LocalDate.of(2026, 1, 15));

        // Check second deposit
        TimeDepositResponse depositResponse2 = deposits[1];
        assertThat(depositResponse2.getId()).isEqualTo(2);
        assertThat(depositResponse2.getPlanType()).isEqualTo("premium");
        assertThat(depositResponse2.getBalance()).isEqualByComparingTo(5000.50);
        assertThat(depositResponse2.getDays()).isEqualTo(180);
        assertThat(depositResponse2.getWithdrawals()).hasSize(2);

        WithdrawalResponse withdrawalResponse2 = depositResponse2.getWithdrawals().get(0);
        assertThat(withdrawalResponse2.getId()).isEqualTo(2);
        assertThat(withdrawalResponse2.getTimeDepositId()).isEqualTo(2);
        assertThat(withdrawalResponse2.getAmount()).isEqualTo(250.75);
        assertThat(withdrawalResponse2.getDate()).isEqualTo(LocalDate.of(2026, 2, 20));

        WithdrawalResponse withdrawalResponse3 = depositResponse2.getWithdrawals().get(1);
        assertThat(withdrawalResponse3.getId()).isEqualTo(3);
        assertThat(withdrawalResponse3.getTimeDepositId()).isEqualTo(2);
        assertThat(withdrawalResponse3.getAmount()).isEqualTo(500.0);
        assertThat(withdrawalResponse3.getDate()).isEqualTo(LocalDate.of(2026, 3, 10));
    }

    @Test
    void testGetAllTimeDeposits_WithEmptyRepository_ReturnsEmptyArray() {
        // Act: Call GET /time-deposits with empty repository
        ResponseEntity<TimeDepositResponse[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/time-deposits",
                TimeDepositResponse[].class
        );

        // Assert: HTTP 200 and empty array
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().isEmpty();
    }
}
