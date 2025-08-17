package com.swiftline.account.infrastructure.persistence.adapter;

import com.swiftline.account.config.ModelMapperConfig;
import com.swiftline.account.domain.model.Transaction;
import com.swiftline.account.infrastructure.persistence.entity.AccountEntity;
import com.swiftline.account.infrastructure.persistence.entity.TransactionEntity;
import com.swiftline.account.infrastructure.persistence.repository.AccountJpaRepository;
import com.swiftline.account.infrastructure.persistence.repository.TransactionJpaRepository;
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

class TransactionRepositoryAdapterTest {

    private TransactionJpaRepository txJpa;
    private AccountJpaRepository accountJpa;
    private TransactionRepositoryAdapter adapter;

    @BeforeEach
    void setup() {
        txJpa = mock(TransactionJpaRepository.class);
        accountJpa = mock(AccountJpaRepository.class);
        ModelMapper mapper = new ModelMapperConfig().modelMapper();
        adapter = new TransactionRepositoryAdapter(txJpa, accountJpa, mapper);
    }

    @Test
    void save_shouldAttachAccount_andReturnDomain() {
        Long accountId = 10L;
        AccountEntity acc = account(accountId);
        when(accountJpa.findById(accountId)).thenReturn(Optional.of(acc));
        when(txJpa.save(any(TransactionEntity.class))).thenAnswer(inv -> {
            TransactionEntity e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });
        Transaction toSave = Transaction.builder()
                .date(LocalDateTime.now())
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("50.00"))
                .balance(new BigDecimal("150.00"))
                .build();
        Transaction out = adapter.save(accountId, toSave);
        assertNotNull(out.getId());
        assertEquals(accountId, out.getAccountId());
    }

    @Test
    void findById_shouldMapAccountId() {
        TransactionEntity e = txEntity(5L, account(7L));
        when(txJpa.findById(5L)).thenReturn(Optional.of(e));
        Optional<Transaction> out = adapter.findById(5L);
        assertTrue(out.isPresent());
        assertEquals(5L, out.get().getId());
        assertEquals(7L, out.get().getAccountId());
    }

    @Test
    void findByAccountId_shouldReturnList() {
        when(txJpa.findByAccount_Id(7L)).thenReturn(List.of(txEntity(1L, account(7L)), txEntity(2L, account(7L))));
        List<Transaction> out = adapter.findByAccountId(7L);
        assertEquals(2, out.size());
        assertEquals(7L, out.get(0).getAccountId());
    }

    @Test
    void update_shouldModifyFields_andReturnDomain() {
        TransactionEntity existing = txEntity(9L, account(1L));
        when(txJpa.findById(9L)).thenReturn(Optional.of(existing));
        when(txJpa.save(any(TransactionEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction newData = Transaction.builder()
                .date(LocalDateTime.now().minusDays(1))
                .transactionType("WITHDRAW")
                .amount(new BigDecimal("20.00"))
                .balance(new BigDecimal("80.00"))
                .build();
        Transaction out = adapter.update(9L, newData);
        assertEquals(9L, out.getId());
        assertEquals("WITHDRAW", out.getTransactionType());
        assertEquals(new BigDecimal("20.00"), out.getAmount());
    }

    @Test
    void deleteById_shouldCallJpa_whenExists() {
        when(txJpa.existsById(3L)).thenReturn(true);
        doNothing().when(txJpa).deleteById(3L);
        adapter.deleteById(3L);
        verify(txJpa).deleteById(3L);
    }

    @Test
    void existsById_shouldDelegate() {
        when(txJpa.existsById(1L)).thenReturn(true);
        assertTrue(adapter.existsById(1L));
    }

    private AccountEntity account(Long id) {
        return AccountEntity.builder()
                .id(id)
                .accountNumber("0001")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("100.00"))
                .status(true)
                .build();
    }

    private TransactionEntity txEntity(Long id, AccountEntity acc) {
        return TransactionEntity.builder()
                .id(id)
                .date(LocalDateTime.now())
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("50.00"))
                .balance(new BigDecimal("150.00"))
                .account(acc)
                .build();
    }
}

