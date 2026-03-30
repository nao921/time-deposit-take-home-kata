package org.ikigaidigital.infrastructure.persistence;

import org.ikigaidigital.application.model.TimeDepositView;
import org.ikigaidigital.application.port.TimeDepositRepository;
import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.Withdrawal;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * JPA adapter implementing the TimeDepositRepository port.
 * Maps between JPA entities and domain objects.
 */
@Component
public class TimeDepositJpaAdapter implements TimeDepositRepository {

    private final TimeDepositJpaRepository timeDepositJpaRepository;
    private final WithdrawalJpaRepository withdrawalJpaRepository;

    public TimeDepositJpaAdapter(TimeDepositJpaRepository timeDepositJpaRepository,
                                WithdrawalJpaRepository withdrawalJpaRepository) {
        this.timeDepositJpaRepository = timeDepositJpaRepository;
        this.withdrawalJpaRepository = withdrawalJpaRepository;
    }

    @Override
    public List<TimeDeposit> findAllDeposits() {
        return timeDepositJpaRepository.findAll()
                .stream()
                .map(this::mapToTimeDeposit)
                .toList();
    }

    @Override
    public List<TimeDepositView> findAllWithWithdrawals() {
        return timeDepositJpaRepository.findAll()
                .stream()
                .map(entity -> new TimeDepositView(
                        mapToTimeDeposit(entity),
                        mapWithdrawals(entity.getWithdrawals())
                ))
                .toList();
    }

    @Override
    public void saveAll(List<TimeDeposit> deposits) {
        List<TimeDepositEntity> entities = deposits.stream()
                .map(this::mapToTimeDepositEntity)
                .toList();
        timeDepositJpaRepository.saveAll(entities);
    }

    private TimeDeposit mapToTimeDeposit(TimeDepositEntity entity) {
        return new TimeDeposit(
                entity.getId(),
                entity.getPlanType(),
                entity.getBalance().doubleValue(),
                entity.getDays()
        );
    }

    private TimeDepositEntity mapToTimeDepositEntity(TimeDeposit deposit) {
        return new TimeDepositEntity(
                deposit.getId(),
                deposit.getPlanType(),
                BigDecimal.valueOf(deposit.getBalance()),
                deposit.getDays()
        );
    }

    private List<Withdrawal> mapWithdrawals(List<WithdrawalEntity> entities) {
        return entities.stream()
                .map(entity -> new Withdrawal(
                        entity.getId(),
                        entity.getTimeDepositId(),
                        entity.getAmount(),
                        entity.getDate()
                ))
                .toList();
    }
}
