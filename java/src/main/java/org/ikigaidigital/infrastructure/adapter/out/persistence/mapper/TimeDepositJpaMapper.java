package org.ikigaidigital.infrastructure.adapter.out.persistence.mapper;

import org.ikigaidigital.domain.timedeposit.TimeDeposit;
import org.ikigaidigital.domain.withdrawal.Withdrawal;
import org.ikigaidigital.infrastructure.adapter.out.persistence.jpa.TimeDepositEntity;
import org.ikigaidigital.infrastructure.adapter.out.persistence.jpa.WithdrawalEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mapper between JPA entities and domain objects.
 * Separates mapping logic from the adapter for better maintainability.
 */
public class TimeDepositJpaMapper {

    public TimeDeposit mapToTimeDeposit(TimeDepositEntity entity) {
        return new TimeDeposit(
                entity.getId(),
                entity.getPlanType(),
                entity.getBalance().doubleValue(),
                entity.getDays()
        );
    }

    public TimeDepositEntity mapToTimeDepositEntity(TimeDeposit deposit) {
        return new TimeDepositEntity(
                deposit.getId(),
                deposit.getPlanType(),
                BigDecimal.valueOf(deposit.getBalance()),
                deposit.getDays()
        );
    }

    public Withdrawal mapToWithdrawal(WithdrawalEntity entity) {
        return new Withdrawal(
                entity.getId(),
                entity.getTimeDepositId(),
                entity.getAmount(),
                entity.getDate()
        );
    }

    public List<Withdrawal> mapWithdrawals(List<WithdrawalEntity> entities) {
        return entities.stream()
                .map(this::mapToWithdrawal)
                .toList();
    }
}
