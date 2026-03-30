package org.ikigaidigital.infrastructure.persistence;

import org.ikigaidigital.application.model.TimeDepositView;
import org.ikigaidigital.application.port.TimeDepositRepository;
import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.Withdrawal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * In-memory implementation of TimeDepositRepository.
 * Stores deposits by ID and withdrawals by deposit ID.
 */
public class InMemoryTimeDepositRepositoryAdapter implements TimeDepositRepository {
    private final Map<Integer, TimeDeposit> deposits = new HashMap<>();
    private final Map<Integer, List<Withdrawal>> withdrawalsByDepositId = new HashMap<>();

    @Override
    public List<TimeDeposit> findAllDeposits() {
        return List.copyOf(deposits.values());
    }

    @Override
    public List<TimeDepositView> findAllWithWithdrawals() {
        return deposits.values().stream()
                .map(deposit -> new TimeDepositView(
                        deposit,
                        withdrawalsByDepositId.getOrDefault(deposit.getId(), List.of())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void saveAll(List<TimeDeposit> depositList) {
        for (TimeDeposit deposit : depositList) {
            deposits.put(deposit.getId(), deposit);
        }
    }

    /**
     * Test helper: seed the repository with deposits and withdrawals.
     */
    public void seed(List<TimeDeposit> seedDeposits, Map<Integer, List<Withdrawal>> seedWithdrawals) {
        deposits.clear();
        withdrawalsByDepositId.clear();
        for (TimeDeposit deposit : seedDeposits) {
            deposits.put(deposit.getId(), deposit);
        }
        withdrawalsByDepositId.putAll(seedWithdrawals);
    }
}
