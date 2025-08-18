package com.swiftline.account.domain.repository;

import com.swiftline.account.domain.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Long accountId, Transaction transaction);
    Optional<Transaction> findById(Long id);
    List<Transaction> findByAccountId(Long accountId);
    Transaction update(Long id, Transaction transaction);
    boolean existsById(Long id);
    Optional<Transaction> findLastByAccountId(Long accountId);
    // nuevo: transacciones por cuenta y rango de fechas (ordenadas por fecha/id asc)
    List<Transaction> findByAccountIdAndDateBetween(Long accountId, LocalDateTime from, LocalDateTime to);
    // nuevo: última transacción antes de una fecha dada
    Optional<Transaction> findLastBeforeDate(Long accountId, LocalDateTime date);
}
