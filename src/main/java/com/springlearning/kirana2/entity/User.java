package com.springlearning.kirana2.entity;

import com.springlearning.kirana2.entity.enums.RoleType;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Getter
public class User {

    /*
    userId, userName, role, phoneNumber, creationDate, balance
     */

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "user_name", nullable = false, unique = true, length = 100)
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private RoleType role;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime creationDate;
    @PrePersist
    void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
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
