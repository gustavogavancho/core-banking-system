package com.swiftline.account.application.service;

import com.swiftline.account.application.dto.TransactionRequest;
import com.swiftline.account.application.exception.NotFoundException;
import com.swiftline.account.application.exception.InsufficientBalanceException;
import com.swiftline.account.domain.model.Transaction;
import com.swiftline.account.domain.repository.AccountRepository;
import com.swiftline.account.domain.repository.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

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
        // Validar existencia de la cuenta y obtener saldo base
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Cuenta no encontrada con id=" + accountId));

        // Obtener el balance anterior: última transacción o saldo inicial de la cuenta
        BigDecimal previousBalance = transactionRepository.findLastByAccountId(accountId)
                .map(Transaction::getBalance)
                .orElse(account.getInitialBalance());

        // Montos positivos/negativos permitidos; calcular nuevo balance
        BigDecimal amount = request.getAmount();
        if (amount == null) {
            throw new IllegalArgumentException("El monto de la transacción es obligatorio");
        }
        BigDecimal newBalance = previousBalance.add(amount);

        // Validar saldo insuficiente
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        // Construir transacción con el balance calculado (ignorando balance del request si vino)
        Transaction tx = toDomain(request);
        tx.setBalance(newBalance);

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
        // Cargar transacción existente para conocer cuenta
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Movimiento no encontrado con id=" + id));

        // Validar cuenta y obtener saldo inicial
        var account = accountRepository.findById(existing.getAccountId())
                .orElseThrow(() -> new NotFoundException("Cuenta no encontrada con id=" + existing.getAccountId()));

        // Construir versión actualizada de la transacción objetivo
        if (request.getAmount() == null) {
            throw new IllegalArgumentException("El monto de la transacción es obligatorio");
        }
        Transaction updatedTarget = toDomain(request);
        updatedTarget.setId(id);
        updatedTarget.setAccountId(existing.getAccountId());
        // balance se recalculará abajo

        // Obtener todas las transacciones de la cuenta y reemplazar la objetivo con los nuevos datos
        List<Transaction> all = new ArrayList<>(transactionRepository.findByAccountId(existing.getAccountId()));
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(id)) {
                all.set(i, updatedTarget);
                break;
            }
        }

        // Ordenar por fecha ascendente y desempatar por id ascendente
        all.sort(Comparator
                .comparing(Transaction::getDate)
                .thenComparing(Transaction::getId));

        // Recalcular balances encadenados desde saldo inicial
        BigDecimal running = account.getInitialBalance();
        Transaction result = null;
        for (Transaction t : all) {
            running = running.add(t.getAmount());
            if (running.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientBalanceException("Insufficient balance");
            }
            t.setBalance(running);
            // Persistir cada transacción con el nuevo balance (y datos actualizados si corresponde)
            Transaction persisted = transactionRepository.update(t.getId(), t);
            if (t.getId().equals(id)) {
                result = persisted;
            }
        }

        // Por seguridad, si no se encontró (no debería pasar), devolver la existente actualizada
        return result != null ? result : transactionRepository.update(id, updatedTarget);
    }

    private Transaction toDomain(TransactionRequest r) {
        return mapper.map(r, Transaction.class);
    }
}
