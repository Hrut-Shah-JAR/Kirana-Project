package com.kiranastore.dto.response;

import com.kiranastore.entity.enums.CurrencyType;
import com.kiranastore.entity.enums.TransactionStatus;
import com.kiranastore.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private final String transactionId;
    private final String userId;
    private final TransactionType transactionType;
    private final CurrencyType currency;
    private final BigDecimal amountPaid;
    private final TransactionStatus transactionStatus;
    private final LocalDateTime creationDate;
    private final String originalTransactionId;

    public TransactionResponse(String transactionId, String userId, TransactionType transactionType,
                               CurrencyType currency, BigDecimal amountPaid,
                               TransactionStatus transactionStatus, LocalDateTime creationDate,
                               String originalTransactionId) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.transactionType = transactionType;
        this.currency = currency;
        this.amountPaid = amountPaid;
        this.transactionStatus = transactionStatus;
        this.creationDate = creationDate;
        this.originalTransactionId = originalTransactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getOriginalTransactionId() {
        return originalTransactionId;
    }

    public String getSelfLink() {
        return "/v1/api/transactions/" + transactionId;
    }

    public String getItemsLink() {
        return "/v1/api/transactions/" + transactionId + "/items";
    }

    public String getRefundLink() {
        return "/v1/api/transactions/" + transactionId + "/refund";
    }
}
