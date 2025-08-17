package com.swiftline.client.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String gender;

    @NotNull
    @Min(0)
    private Integer age;

    @NotBlank
    private String identification;

    @NotBlank
    private String address;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String password;

    @NotNull
    private Boolean status;
}

