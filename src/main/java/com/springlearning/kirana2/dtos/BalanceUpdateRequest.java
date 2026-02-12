package com.springlearning.kirana2.dtos;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class BalanceUpdateRequest {

    @NotBlank
    @Size(max = 100)
    private String username;

    @NotNull
    @Digits(integer = 12, fraction = 2)
    private BigDecimal balance;

    public BalanceUpdateRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
