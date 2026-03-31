package org.ikigaidigital.infrastructure.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeDepositJpaRepository extends JpaRepository<TimeDepositEntity, Integer> {
}
