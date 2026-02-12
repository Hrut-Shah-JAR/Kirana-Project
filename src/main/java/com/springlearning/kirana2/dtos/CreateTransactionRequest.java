package com.springlearning.kirana2.dtos;

import com.springlearning.kirana2.entity.enums.CurrencyType;
import com.springlearning.kirana2.entity.enums.TransactionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public class CreateTransactionRequest {

    @NotBlank
    @Size(max = 36)
    private String userId;

    @NotNull
    private TransactionType transactionType;

    @NotNull
    private CurrencyType currency;

    @DecimalMin("0.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amountPaid;

    @Size(max = 36)
    private String originalTransactionId;

    @NotEmpty
    @Valid
    private List<CreateTransactionItemRequest> items;

    public CreateTransactionRequest() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getOriginalTransactionId() {
        return originalTransactionId;
    }

    public void setOriginalTransactionId(String originalTransactionId) {
        this.originalTransactionId = originalTransactionId;
    }

    public List<CreateTransactionItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CreateTransactionItemRequest> items) {
        this.items = items;
    }
}
