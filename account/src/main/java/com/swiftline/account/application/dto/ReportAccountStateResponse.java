package com.swiftline.account.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportAccountStateResponse {
    private Long clientId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime from;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime to;
    private List<AccountReport> accounts;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountReport {
        private Long accountId;
        private String accountNumber;
        private String accountType;
        private BigDecimal initialBalance; // saldo inicial de la cuenta
        private BigDecimal balance; // balance al final del rango (to)
        private List<TransactionItem> transactions; // transacciones dentro del rango
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransactionItem {
        private Long id;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime date;
        private String transactionType;
        private BigDecimal amount;
        private BigDecimal balance; // balance de la transacción
    }
}
