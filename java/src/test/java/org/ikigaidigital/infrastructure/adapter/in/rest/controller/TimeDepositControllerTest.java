package org.ikigaidigital.infrastructure.adapter.in.rest.controller;

import org.ikigaidigital.infrastructure.adapter.out.persistence.jpa.TimeDepositEntity;
import org.ikigaidigital.infrastructure.adapter.out.persistence.jpa.TimeDepositJpaRepository;
import org.ikigaidigital.infrastructure.adapter.out.persistence.jpa.WithdrawalEntity;
import org.ikigaidigital.infrastructure.adapter.out.persistence.jpa.WithdrawalJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class TimeDepositControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TimeDepositJpaRepository timeDepositRepository;

    @Autowired
    private WithdrawalJpaRepository withdrawalRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        withdrawalRepository.deleteAll();
        timeDepositRepository.deleteAll();
    }

    @Nested
    class GetAllTimeDeposits {

        @Test
        void getAll_returnsEmptyListWhenNoDeposits() throws Exception {
            mockMvc.perform(get("/time-deposits"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        void getAll_returnsDepositsWithCorrectStructure() throws Exception {
            TimeDepositEntity deposit1 = new TimeDepositEntity(1, "basic", BigDecimal.valueOf(1200.00), 31);
            TimeDepositEntity deposit2 = new TimeDepositEntity(2, "premium", BigDecimal.valueOf(5000.00), 46);
            timeDepositRepository.save(deposit1);
            timeDepositRepository.save(deposit2);

            WithdrawalEntity withdrawal = new WithdrawalEntity(101, 1, 100.00, LocalDate.of(2026, 1, 15));
            withdrawalRepository.save(withdrawal);

            mockMvc.perform(get("/time-deposits"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].planType", is("basic")))
                    .andExpect(jsonPath("$[0].balance", is(1200.00)))
                    .andExpect(jsonPath("$[0].days", is(31)))
                    .andExpect(jsonPath("$[0].withdrawals", hasSize(1)))
                    .andExpect(jsonPath("$[0].withdrawals[0].id", is(101)))
                    .andExpect(jsonPath("$[0].withdrawals[0].amount", is(100.00)))
                    .andExpect(jsonPath("$[1].id", is(2)))
                    .andExpect(jsonPath("$[1].withdrawals", hasSize(0)));
        }
    }

    @Nested
    class UpdateBalances {

        @Test
        void updateBalances_returns200WithEmptyBody() throws Exception {
            TimeDepositEntity deposit = new TimeDepositEntity(1, "basic", BigDecimal.valueOf(1200.00), 31);
            timeDepositRepository.save(deposit);

            mockMvc.perform(post("/time-deposits/update-balances"))
                    .andExpect(status().isOk());
        }

        @Test
        void updateBalances_updatesDepositBalances() throws Exception {
            TimeDepositEntity deposit1 = new TimeDepositEntity(1, "basic", BigDecimal.valueOf(1200.00), 31);
            TimeDepositEntity deposit2 = new TimeDepositEntity(2, "premium", BigDecimal.valueOf(5000.00), 46);
            timeDepositRepository.save(deposit1);
            timeDepositRepository.save(deposit2);

            // Update balances
            mockMvc.perform(post("/time-deposits/update-balances"))
                    .andExpect(status().isOk());

            // Verify balances were updated
            mockMvc.perform(get("/time-deposits"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].balance", is(1201.00)))
                    .andExpect(jsonPath("$[1].balance", is(5020.83)));
        }
    }

    @Nested
    class OpenAPIVerification {

        @Test
        void openApiDocs_returns200() throws Exception {
            mockMvc.perform(get("/v3/api-docs"))
                    .andExpect(status().isOk());
        }
    }
}
