package com.kiranastore.exception;

public class InvalidCredentialsException extends UnauthorizedException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
