package com.kiranastore.dto;

import java.math.BigDecimal;

public class TransactionItemResponse {

    private final String transactionItemId;
    private final String transactionId;
    private final String productId;
    private final String productName;
    private final BigDecimal quantity;
    private final BigDecimal unitPriceAtSale;

    public TransactionItemResponse(String transactionItemId, String transactionId, String productId,
                                   String productName, BigDecimal quantity, BigDecimal unitPriceAtSale) {
        this.transactionItemId = transactionItemId;
        this.transactionId = transactionId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPriceAtSale = unitPriceAtSale;
    }

    public String getTransactionItemId() {
        return transactionItemId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPriceAtSale() {
        return unitPriceAtSale;
    }
}
