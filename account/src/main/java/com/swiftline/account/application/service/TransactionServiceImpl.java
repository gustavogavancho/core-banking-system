package com.swiftline.account.application.service;

import com.swiftline.account.application.dto.TransactionRequest;
import com.swiftline.account.application.exception.NotFoundException;
import com.swiftline.account.domain.model.Transaction;
import com.swiftline.account.domain.repository.AccountRepository;
import com.swiftline.account.domain.repository.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper mapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  AccountRepository accountRepository,
                                  ModelMapper mapper) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.mapper = mapper;
    }

    @Override
    public Transaction create(Long accountId, TransactionRequest request) {
        if (!accountRepository.existsById(accountId)) {
            throw new NotFoundException("Cuenta no encontrada con id=" + accountId);
        }
        Transaction tx = toDomain(request);
        return transactionRepository.save(accountId, tx);
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction get(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Movimiento no encontrado con id=" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> listByAccount(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new NotFoundException("Cuenta no encontrada con id=" + accountId);
        }
        return transactionRepository.findByAccountId(accountId);
    }

    @Override
    public Transaction update(Long id, TransactionRequest request) {
        if (!transactionRepository.existsById(id)) {
            throw new NotFoundException("Movimiento no encontrado con id=" + id);
        }
        Transaction tx = toDomain(request);
        return transactionRepository.update(id, tx);
    }

    @Override
    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new NotFoundException("Movimiento no encontrado con id=" + id);
        }
        transactionRepository.deleteById(id);
    }

    private Transaction toDomain(TransactionRequest r) {
        return mapper.map(r, Transaction.class);
    }
}

