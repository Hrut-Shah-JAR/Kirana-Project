package com.kiranastore.dto.response;

import com.kiranastore.entity.enums.RoleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserResponse {

    private final String userId;
    private final String username;
    private final RoleType role;
    private final String phoneNumber;
    private final LocalDateTime createdOn;
    private final BigDecimal balance;

    public UserResponse(String userId, String username, RoleType role, String phoneNumber,
                        LocalDateTime createdOn, BigDecimal balance) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.createdOn = createdOn;
        this.balance = balance;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public RoleType getRole() {
        return role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
