package org.ikigaidigital.infrastructure.adapter.out.persistence.jpa;

import org.ikigaidigital.application.model.TimeDepositView;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.timedeposit.TimeDeposit;
import org.ikigaidigital.infrastructure.adapter.out.persistence.mapper.TimeDepositJpaMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JPA adapter implementing the TimeDepositRepository port.
 * Maps between JPA entities and domain objects.
 * This is a driven adapter (called by the application layer).
 */
@Component
public class TimeDepositJpaAdapter implements TimeDepositRepository {

    private final TimeDepositJpaRepository timeDepositJpaRepository;
    private final WithdrawalJpaRepository withdrawalJpaRepository;
    private final TimeDepositJpaMapper mapper;

    public TimeDepositJpaAdapter(TimeDepositJpaRepository timeDepositJpaRepository,
                                 WithdrawalJpaRepository withdrawalJpaRepository,
                                 TimeDepositJpaMapper mapper) {
        this.timeDepositJpaRepository = timeDepositJpaRepository;
        this.withdrawalJpaRepository = withdrawalJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<TimeDeposit> findAllDeposits() {
        return timeDepositJpaRepository.findAll()
                .stream()
                .map(mapper::mapToTimeDeposit)
                .toList();
    }

    @Override
    public List<TimeDepositView> findAllWithWithdrawals() {
        return timeDepositJpaRepository.findAll()
                .stream()
                .map(entity -> new TimeDepositView(
                        mapper.mapToTimeDeposit(entity),
                        mapper.mapWithdrawals(entity.getWithdrawals())
                ))
                .toList();
    }

    @Override
    public void saveAll(List<TimeDeposit> deposits) {
        List<TimeDepositEntity> entities = deposits.stream()
                .map(mapper::mapToTimeDepositEntity)
                .toList();
        timeDepositJpaRepository.saveAll(entities);
    }
}
