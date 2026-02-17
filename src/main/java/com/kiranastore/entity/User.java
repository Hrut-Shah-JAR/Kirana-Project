package com.kiranastore.entity;

import com.kiranastore.entity.enums.RoleType;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Getter
@EntityListeners(AuditingEntityListener.class)
public class User {

    /*
    userId, userName, role, phoneNumber, creationDate, balance
     */

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false, length = 36)
    private String id;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private RoleType role;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @PrePersist
    void onCreate() {
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
    }

    @Column(name = "balance", precision = 12, scale = 2, nullable = false)
    private BigDecimal balance;

    protected User() {
    }

    public User(String userName, RoleType role, String passwordHash, String phoneNumber) {
        this.userName = userName;
        this.role = role;
        this.passwordHash = passwordHash;
        this.phoneNumber = phoneNumber;
    }

    public void updateBalance(BigDecimal newBalance) {
        this.balance = newBalance;
    }

    public void updatePasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
