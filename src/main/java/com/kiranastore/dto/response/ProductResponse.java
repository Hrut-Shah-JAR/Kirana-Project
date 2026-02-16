package com.kiranastore.dto.response;

import java.math.BigDecimal;

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

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
}
