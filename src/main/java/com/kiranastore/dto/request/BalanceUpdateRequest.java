package com.kiranastore.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class BalanceUpdateRequest {

    @NotBlank
    @Size(max = 100)
    private String username;

    @NotBlank
    @Size (max = 100)
    private String updateType;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer = 12, fraction = 2)
    private BigDecimal balance;

    public BalanceUpdateRequest() {
    }

}
