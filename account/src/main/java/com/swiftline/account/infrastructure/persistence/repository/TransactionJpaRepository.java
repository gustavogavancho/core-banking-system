package com.swiftline.account.infrastructure.persistence.repository;

import com.swiftline.account.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByAccount_Id(Long accountId);
    // Última transacción registrada para una cuenta (por fecha y desempate por id)
    Optional<TransactionEntity> findTopByAccount_IdOrderByDateDescIdDesc(Long accountId);
}
