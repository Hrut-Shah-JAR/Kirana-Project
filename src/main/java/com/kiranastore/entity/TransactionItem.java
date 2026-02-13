package com.kiranastore.entity;


import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
@Table (name = "transaction_items")
public class TransactionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_item_id", nullable = false, length = 36)
    private String transactionItemId;

    //Foreign key for transaction ID
    @Column(name = "transaction_id", nullable = false, length = 36)
    private String transactionForeignId;

    //Foreign key for product ID
    @Column(name = "product_id", nullable = false, length = 36)
    private String productForeignId;

    @Column(name = "quantity", precision = 10, scale = 2, nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit_price_at_sale", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPriceAtSale;

    protected TransactionItem() {
    }

    public TransactionItem(String transactionForeignId, String productForeignId, BigDecimal quantity,
                           BigDecimal unitPriceAtSale) {
        this.transactionForeignId = transactionForeignId;
        this.productForeignId = productForeignId;
        this.quantity = quantity;
        this.unitPriceAtSale = unitPriceAtSale;
    }
}
