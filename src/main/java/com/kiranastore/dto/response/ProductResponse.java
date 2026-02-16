package com.kiranastore.dto.response;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductResponse {

    private final String productId;
    private final String productName;
    private final String category;
    private final BigDecimal price;
    private final BigDecimal quantity;

    public ProductResponse(String productId, String productName, String category, BigDecimal price,
                           BigDecimal quantity) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

}
