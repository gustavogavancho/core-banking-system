package com.swiftline.account.infrastructure.persistence.adapter;

import com.swiftline.account.domain.model.Transaction;
import com.swiftline.account.domain.repository.TransactionRepository;
import com.swiftline.account.infrastructure.persistence.entity.AccountEntity;
import com.swiftline.account.infrastructure.persistence.entity.TransactionEntity;
import com.swiftline.account.infrastructure.persistence.repository.AccountJpaRepository;
import com.swiftline.account.infrastructure.persistence.repository.TransactionJpaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final TransactionJpaRepository transactionJpaRepository;
    private final AccountJpaRepository accountJpaRepository;
    private final ModelMapper mapper;

    public TransactionRepositoryAdapter(TransactionJpaRepository transactionJpaRepository,
                                        AccountJpaRepository accountJpaRepository,
                                        ModelMapper mapper) {
        this.transactionJpaRepository = transactionJpaRepository;
        this.accountJpaRepository = accountJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Transaction save(Long accountId, Transaction transaction) {
        AccountEntity account = accountJpaRepository.findById(accountId).orElseThrow();
        TransactionEntity entity = toEntity(transaction);
        entity.setAccount(account);
        TransactionEntity saved = transactionJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> findById(Long id) {
        return transactionJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findByAccountId(Long accountId) {
        return transactionJpaRepository.findByAccount_Id(accountId).stream().map(this::toDomain).toList();
    }

    @Override
    public Transaction update(Long id, Transaction transaction) {
        TransactionEntity existing = transactionJpaRepository.findById(id).orElseThrow();
        existing.setDate(transaction.getDate());
        existing.setTransactionType(transaction.getTransactionType());
        existing.setAmount(transaction.getAmount());
        existing.setBalance(transaction.getBalance());
        TransactionEntity saved = transactionJpaRepository.save(existing);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        if (transactionJpaRepository.existsById(id)) {
            transactionJpaRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsById(Long id) {
        return transactionJpaRepository.existsById(id);
    }

    private Transaction toDomain(TransactionEntity entity) {
        return mapper.map(entity, Transaction.class);
    }

    private TransactionEntity toEntity(Transaction transaction) {
        return mapper.map(transaction, TransactionEntity.class);
    }
}

