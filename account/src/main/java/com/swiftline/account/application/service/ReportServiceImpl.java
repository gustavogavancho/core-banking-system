package com.swiftline.account.application.service;

import com.swiftline.account.application.dto.ReportAccountStateResponse;
import com.swiftline.account.application.exception.ClientNotFoundException;
import com.swiftline.account.domain.model.Account;
import com.swiftline.account.domain.model.Transaction;
import com.swiftline.account.domain.repository.AccountRepository;
import com.swiftline.account.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ClientValidationService clientValidationService;

    public ReportServiceImpl(AccountRepository accountRepository,
                             TransactionRepository transactionRepository,
                             ClientValidationService clientValidationService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.clientValidationService = clientValidationService;
    }

    @Override
    public ReportAccountStateResponse generate(Long clientId, LocalDateTime from, LocalDateTime to) {
        if (clientId == null) {
            throw new IllegalArgumentException("clientId es obligatorio");
        }
        if (from == null || to == null) {
            throw new IllegalArgumentException("from y to son obligatorios");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("El rango de fechas es inválido (to < from)");
        }
        if (!clientValidationService.existsById(clientId)) {
            throw new ClientNotFoundException(clientId);
        }

        List<Account> accounts = accountRepository.findByClientId(clientId);

        List<ReportAccountStateResponse.AccountReport> accountReports = accounts.stream().map(acc -> {
            // Transacciones dentro del rango
            List<Transaction> txsInRange = transactionRepository
                    .findByAccountIdAndDateBetween(acc.getId(), from, to);

            // Balance al final del rango: última transacción <= to, si no hay usar saldo inicial
            var lastTo = transactionRepository.findLastBeforeDate(acc.getId(), to);
            var balanceAtTo = lastTo.map(Transaction::getBalance).orElse(acc.getInitialBalance());

            List<ReportAccountStateResponse.TransactionItem> items = txsInRange.stream()
                    .map(t -> ReportAccountStateResponse.TransactionItem.builder()
                            .id(t.getId())
                            .date(t.getDate())
                            .transactionType(t.getTransactionType())
                            .amount(t.getAmount())
                            .balance(t.getBalance())
                            .build())
                    .collect(Collectors.toList());

            return ReportAccountStateResponse.AccountReport.builder()
                    .accountId(acc.getId())
                    .accountNumber(acc.getAccountNumber())
                    .accountType(acc.getAccountType())
                    .initialBalance(acc.getInitialBalance())
                    .balance(balanceAtTo)
                    .transactions(items)
                    .build();
        }).collect(Collectors.toList());

        return ReportAccountStateResponse.builder()
                .clientId(clientId)
                .from(from)
                .to(to)
                .accounts(accountReports)
                .build();
    }
}
