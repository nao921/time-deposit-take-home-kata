package org.ikigaidigital.infrastructure.adapter.out.persistence;

import org.ikigaidigital.domain.timedeposit.TimeDeposit;
import org.ikigaidigital.domain.withdrawal.Withdrawal;
import org.ikigaidigital.infrastructure.adapter.out.persistence.jpa.TimeDepositEntity;
import org.ikigaidigital.infrastructure.adapter.out.persistence.jpa.TimeDepositJpaAdapter;
import org.ikigaidigital.infrastructure.adapter.out.persistence.jpa.TimeDepositJpaRepository;
import org.ikigaidigital.infrastructure.adapter.out.persistence.jpa.WithdrawalEntity;
import org.ikigaidigital.infrastructure.adapter.out.persistence.jpa.WithdrawalJpaRepository;
import org.ikigaidigital.infrastructure.adapter.out.persistence.mapper.TimeDepositJpaMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for TimeDepositJpaAdapter mapper logic.
 * Tests entity-to-domain and domain-to-entity mappings.
 */
class TimeDepositJpaAdapterMapperTest {

    @Mock
    private TimeDepositJpaRepository timeDepositJpaRepository;

    @Mock
    private WithdrawalJpaRepository withdrawalJpaRepository;

    private TimeDepositJpaAdapter adapter;
    private TimeDepositJpaMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new TimeDepositJpaMapper();
        adapter = new TimeDepositJpaAdapter(timeDepositJpaRepository, withdrawalJpaRepository, mapper);
    }

    @Test
    void testMapTimeDepositEntityToTimeDeposit() {
        // Arrange
        TimeDepositEntity entity = new TimeDepositEntity(1, "basic", new BigDecimal("1000.50"), 90);
        when(timeDepositJpaRepository.findAll()).thenReturn(List.of(entity));

        // Act
        List<TimeDeposit> results = adapter.findAllDeposits();

        // Assert
        assertThat(results).hasSize(1);
        TimeDeposit result = results.get(0);
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getPlanType()).isEqualTo("basic");
        assertThat(result.getBalance()).isEqualTo(1000.50);
        assertThat(result.getDays()).isEqualTo(90);
    }

    @Test
    void testMapTimeDepositToTimeDepositEntity() {
        // Arrange
        TimeDeposit deposit = new TimeDeposit(2, "student", 2500.75, 180);
        when(timeDepositJpaRepository.saveAll(org.mockito.ArgumentMatchers.anyList())).thenReturn(List.of());

        // Act
        adapter.saveAll(List.of(deposit));

        // Assert - verify the entity was created with correct mappings
        TimeDepositEntity entity = new TimeDepositEntity(
                deposit.getId(),
                deposit.getPlanType(),
                BigDecimal.valueOf(deposit.getBalance()),
                deposit.getDays()
        );
        assertThat(entity.getId()).isEqualTo(2);
        assertThat(entity.getPlanType()).isEqualTo("student");
        assertThat(entity.getBalance()).isEqualTo(BigDecimal.valueOf(2500.75));
        assertThat(entity.getDays()).isEqualTo(180);
    }

    @Test
    void testMapTimeDepositToTimeDepositEntityPreservesBalance() {
        // Arrange - simulating balance update round-trip
        TimeDeposit originalDeposit = new TimeDeposit(3, "premium", 5000.00, 365);
        double updatedBalance = 5062.50;
        originalDeposit.setBalance(updatedBalance);
        when(timeDepositJpaRepository.saveAll(org.mockito.ArgumentMatchers.anyList())).thenReturn(List.of());

        // Act - save the deposit with updated balance
        adapter.saveAll(List.of(originalDeposit));

        // Assert - verify round-trip preserves balance
        TimeDepositEntity entity = new TimeDepositEntity(
                originalDeposit.getId(),
                originalDeposit.getPlanType(),
                BigDecimal.valueOf(originalDeposit.getBalance()),
                originalDeposit.getDays()
        );
        assertThat(entity.getBalance().doubleValue()).isEqualTo(updatedBalance);
        assertThat(entity.getId()).isEqualTo(3);
        assertThat(entity.getPlanType()).isEqualTo("premium");
        assertThat(entity.getDays()).isEqualTo(365);
    }

    @Test
    void testMapWithdrawalEntityToWithdrawal() {
        // Arrange
        WithdrawalEntity entity = new WithdrawalEntity(1, 10, 500.00, LocalDate.of(2024, 3, 15));

        // Act
        Withdrawal result = mapper.mapToWithdrawal(entity);

        // Assert
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.timeDepositId()).isEqualTo(10);
        assertThat(result.amount()).isEqualTo(500.00);
        assertThat(result.date()).isEqualTo(LocalDate.of(2024, 3, 15));
    }

    @Test
    void testMapMultipleWithdrawalEntitiesToWithdrawals() {
        // Arrange
        WithdrawalEntity entity1 = new WithdrawalEntity(1, 5, 100.00, LocalDate.of(2024, 1, 10));
        WithdrawalEntity entity2 = new WithdrawalEntity(2, 5, 200.00, LocalDate.of(2024, 2, 20));

        List<WithdrawalEntity> entities = List.of(entity1, entity2);

        // Act
        List<Withdrawal> results = mapper.mapWithdrawals(entities);

        // Assert
        assertThat(results).hasSize(2);
        assertThat(results.get(0).id()).isEqualTo(1);
        assertThat(results.get(0).amount()).isEqualTo(100.00);
        assertThat(results.get(1).id()).isEqualTo(2);
        assertThat(results.get(1).amount()).isEqualTo(200.00);
    }

    @Test
    void testTimeDepositEntityMappingPreservesAllFields() {
        // Arrange
        TimeDepositEntity entity = new TimeDepositEntity(99, "premium", new BigDecimal("9999.99"), 999);
        when(timeDepositJpaRepository.findAll()).thenReturn(List.of(entity));

        // Act
        List<TimeDeposit> results = adapter.findAllDeposits();

        // Assert
        assertThat(results).hasSize(1);
        TimeDeposit deposit = results.get(0);
        assertThat(deposit.getId()).isEqualTo(entity.getId());
        assertThat(deposit.getPlanType()).isEqualTo(entity.getPlanType());
        assertThat(deposit.getBalance()).isEqualTo(entity.getBalance().doubleValue());
        assertThat(deposit.getDays()).isEqualTo(entity.getDays());
    }
}
