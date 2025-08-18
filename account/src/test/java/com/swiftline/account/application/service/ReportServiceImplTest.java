package com.swiftline.account.application.service;

import com.swiftline.account.application.dto.ReportAccountStateResponse;
import com.swiftline.account.application.exception.ClientNotFoundException;
import com.swiftline.account.domain.model.Account;
import com.swiftline.account.domain.model.Transaction;
import com.swiftline.account.domain.repository.AccountRepository;
import com.swiftline.account.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceImplTest {

    private AccountRepository accountRepo;
    private TransactionRepository txRepo;
    private ClientValidationService clientValidation;
    private ReportService service;

    @BeforeEach
    void setup() {
        accountRepo = mock(AccountRepository.class);
        txRepo = mock(TransactionRepository.class);
        clientValidation = mock(ClientValidationService.class);
        service = new ReportServiceImpl(accountRepo, txRepo, clientValidation);
    }

    @Test
    void generate_shouldReturnReport_withInitialAndFinalBalance_andTransactionsInRange() {
        Long clientId = 123L;
        LocalDateTime from = LocalDateTime.of(2024, 1, 10, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 1, 12, 23, 59, 59);

        when(clientValidation.existsById(clientId)).thenReturn(true);
        when(accountRepo.findByClientId(clientId)).thenReturn(List.of(
                Account.builder()
                        .id(1L)
                        .accountNumber("0001")
                        .accountType("SAVINGS")
                        .initialBalance(new BigDecimal("100.00"))
                        .status(true)
                        .clientId(clientId)
                        .build()
        ));

        // Transacciones en rango
        Transaction t1 = Transaction.builder()
                .id(10L)
                .accountId(1L)
                .date(LocalDateTime.of(2024, 1, 10, 10, 0))
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("50.00"))
                .balance(new BigDecimal("150.00"))
                .build();
        Transaction t2 = Transaction.builder()
                .id(11L)
                .accountId(1L)
                .date(LocalDateTime.of(2024, 1, 11, 10, 0))
                .transactionType("WITHDRAW")
                .amount(new BigDecimal("-20.00"))
                .balance(new BigDecimal("130.00"))
                .build();
        when(txRepo.findByAccountIdAndDateBetween(1L, from, to)).thenReturn(List.of(t1, t2));
        when(txRepo.findLastBeforeDate(1L, to)).thenReturn(Optional.of(t2));

        ReportAccountStateResponse out = service.generate(clientId, from, to);
        assertEquals(clientId, out.getClientId());
        assertEquals(from, out.getFrom());
        assertEquals(to, out.getTo());
        assertEquals(1, out.getAccounts().size());
        var accReport = out.getAccounts().get(0);
        assertEquals(1L, accReport.getAccountId());
        assertEquals("0001", accReport.getAccountNumber());
        assertEquals("SAVINGS", accReport.getAccountType());
        assertEquals(new BigDecimal("100.00"), accReport.getInitialBalance());
        assertEquals(new BigDecimal("130.00"), accReport.getBalance());
        assertEquals(2, accReport.getTransactions().size());
        assertEquals(10L, accReport.getTransactions().get(0).getId());
        assertEquals(new BigDecimal("50.00"), accReport.getTransactions().get(0).getAmount());
    }

    @Test
    void generate_shouldUseInitialBalance_whenNoTransactionsBeforeOrAtTo() {
        Long clientId = 123L;
        LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 1, 5, 23, 59, 59);

        when(clientValidation.existsById(clientId)).thenReturn(true);
        when(accountRepo.findByClientId(clientId)).thenReturn(List.of(
                Account.builder()
                        .id(2L)
                        .accountNumber("0002")
                        .accountType("CHECKING")
                        .initialBalance(new BigDecimal("250.00"))
                        .status(true)
                        .clientId(clientId)
                        .build()
        ));

        when(txRepo.findByAccountIdAndDateBetween(2L, from, to)).thenReturn(List.of());
        when(txRepo.findLastBeforeDate(2L, to)).thenReturn(Optional.empty());

        ReportAccountStateResponse out = service.generate(clientId, from, to);
        var accReport = out.getAccounts().get(0);
        assertEquals(new BigDecimal("250.00"), accReport.getBalance());
        assertTrue(accReport.getTransactions().isEmpty());
    }

    @Test
    void generate_shouldThrow_whenClientNotFound() {
        Long clientId = 999L;
        LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 1, 1, 1, 0);
        when(clientValidation.existsById(clientId)).thenReturn(false);
        assertThrows(ClientNotFoundException.class, () -> service.generate(clientId, from, to));
    }
}

