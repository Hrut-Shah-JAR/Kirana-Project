package com.kiranastore.dto.response;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
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

    public String getTransactionLink() {
        return "/v1/api/transactions/" + transactionId;
    }

    public String getProductLink() {
        return "/v1/api/products/" + productId;
    }

}
