package com.kiranastore.entity;


import com.kiranastore.entity.enums.CurrencyType;
import com.kiranastore.entity.enums.TransactionStatus;
import com.kiranastore.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Table (name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id", nullable = false, length = 36)
    private String id;

    // Foreign key for UserId
    @Column(name = "user_id", length = 36, nullable = false)
    private String userForeignId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 30)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 10)
    private CurrencyType currency;

    @Column(name = "amount_paid", precision = 10, scale = 2, nullable = false)
    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false, length = 30)
    private TransactionStatus transactionStatus;


    //    Use Data from Java instead of LocalDateTime
    @Column(name = "created_at", nullable = false)
    //    @CreatedDate
    private LocalDateTime creationDate;
    @PrePersist
    void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
    }

    // The original transaction ID in case of a refund
    @Column(name = "original_transaction_id", length = 36)
    private String originalTransactionId;

    protected Transaction() {
    }

    public Transaction(String userForeignId, TransactionType transactionType, CurrencyType currency,
                       BigDecimal amountPaid, TransactionStatus transactionStatus,
                       String originalTransactionId) {
        this.userForeignId = userForeignId;
        this.transactionType = transactionType;
        this.currency = currency;
        this.amountPaid = amountPaid;
        this.transactionStatus = transactionStatus;
        this.originalTransactionId = originalTransactionId;
    }
}
