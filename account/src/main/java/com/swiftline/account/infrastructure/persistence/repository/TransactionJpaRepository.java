package com.swiftline.account.infrastructure.persistence.repository;

import com.swiftline.account.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByAccount_Id(Long accountId);
    // Última transacción registrada para una cuenta (por fecha y desempate por id)
    Optional<TransactionEntity> findTopByAccount_IdOrderByDateDescIdDesc(Long accountId);

    // Rango de fechas ordenado ascendente
    List<TransactionEntity> findByAccount_IdAndDateBetweenOrderByDateAscIdAsc(Long accountId, LocalDateTime from, LocalDateTime to);

    // Última antes o igual a una fecha específica
    Optional<TransactionEntity> findTopByAccount_IdAndDateLessThanEqualOrderByDateDescIdDesc(Long accountId, LocalDateTime date);
}
