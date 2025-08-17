package com.swiftline.account.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequest {
    @NotBlank
    private String accountNumber;

    @NotBlank
    private String accountType;

    @NotNull
    private BigDecimal initialBalance;

    @NotNull
    private Boolean status;

    @NotNull
    private Long clientId;
}
