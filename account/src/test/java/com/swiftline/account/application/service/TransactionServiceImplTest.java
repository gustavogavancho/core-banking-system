package com.swiftline.account.application.service;

import com.swiftline.account.application.dto.TransactionRequest;
import com.swiftline.account.application.exception.NotFoundException;
import com.swiftline.account.domain.model.Transaction;
import com.swiftline.account.domain.repository.AccountRepository;
import com.swiftline.account.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    private TransactionRepository txRepo;
    private AccountRepository accountRepo;
    private TransactionService service;

    @BeforeEach
    void setup() {
        txRepo = mock(TransactionRepository.class);
        accountRepo = mock(AccountRepository.class);
        service = new TransactionServiceImpl(txRepo, accountRepo, new ModelMapper());
    }

    @Test
    void create_shouldSave_whenAccountExists() {
        Long accountId = 10L;
        when(accountRepo.existsById(accountId)).thenReturn(true);
        when(txRepo.save(eq(accountId), any(Transaction.class))).thenAnswer(inv -> {
            Transaction t = inv.getArgument(1);
            t.setId(1L);
            t.setAccountId(accountId);
            return t;
        });
        TransactionRequest req = req();
        Transaction out = service.create(accountId, req);
        assertNotNull(out.getId());
        assertEquals(accountId, out.getAccountId());
        assertEquals(req.getTransactionType(), out.getTransactionType());
    }

    @Test
    void create_shouldThrow_whenAccountMissing() {
        when(accountRepo.existsById(99L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.create(99L, req()));
    }

    @Test
    void get_shouldReturn_whenFound() {
        when(txRepo.findById(5L)).thenReturn(Optional.of(Transaction.builder().id(5L).build()));
        Transaction out = service.get(5L);
        assertEquals(5L, out.getId());
    }

    @Test
    void get_shouldThrow_whenMissing() {
        when(txRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.get(99L));
    }

    @Test
    void listByAccount_shouldReturn_whenAccountExists() {
        when(accountRepo.existsById(7L)).thenReturn(true);
        when(txRepo.findByAccountId(7L)).thenReturn(List.of(Transaction.builder().id(1L).accountId(7L).build()));
        List<Transaction> out = service.listByAccount(7L);
        assertEquals(1, out.size());
        assertEquals(7L, out.get(0).getAccountId());
    }

    @Test
    void listByAccount_shouldThrow_whenAccountMissing() {
        when(accountRepo.existsById(7L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.listByAccount(7L));
    }

    @Test
    void update_shouldCallRepo_whenExists() {
        when(txRepo.existsById(7L)).thenReturn(true);
        when(txRepo.update(eq(7L), any(Transaction.class)))
                .thenReturn(Transaction.builder().id(7L).build());
        Transaction out = service.update(7L, req());
        assertEquals(7L, out.getId());
    }

    @Test
    void update_shouldThrow_whenMissing() {
        when(txRepo.existsById(7L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.update(7L, req()));
    }

    private TransactionRequest req() {
        return TransactionRequest.builder()
                .date(LocalDateTime.now())
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("50.00"))
                .balance(new BigDecimal("150.00"))
                .build();
    }
}
