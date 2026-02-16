package com.kiranastore.dto.request;

import java.math.BigDecimal;


//Created for purposes of extending pageable functionality in the future

public class ProductSearchRequest extends PageRequestDto {

    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    public ProductSearchRequest() {
    }

    public ProductSearchRequest(Integer page, Integer size, String category, BigDecimal minPrice,
                                BigDecimal maxPrice) {
        super(page, size);
        this.category = category;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }
}
