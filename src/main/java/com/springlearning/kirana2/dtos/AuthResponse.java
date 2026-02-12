package com.springlearning.kirana2.dtos;

public class AuthResponse {

    private final String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
