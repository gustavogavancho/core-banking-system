package com.swiftline.account.domain.repository;

import com.swiftline.account.domain.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Long accountId, Transaction transaction);
    Optional<Transaction> findById(Long id);
    List<Transaction> findByAccountId(Long accountId);
    Transaction update(Long id, Transaction transaction);
    boolean existsById(Long id);
    Optional<Transaction> findLastByAccountId(Long accountId);
}
