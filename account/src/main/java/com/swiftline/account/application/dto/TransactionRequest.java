package com.swiftline.account.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {
    @NotNull
    private LocalDateTime date;

    @NotBlank
    private String transactionType;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private BigDecimal balance;
}

