package com.kiranastore.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<FieldViolation> violations = new ArrayList<>();

        //Converted for loop to for-each loop
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                violations.add(new FieldViolation(fieldError.getField(), fieldError.getDefaultMessage()))
        );

        ValidationErrorResponse response = new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                request.getRequestURI(),
                violations
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        List<FieldViolation> violations = new ArrayList<>();
        ex.getConstraintViolations().forEach(violation ->
                violations.add(new FieldViolation(violation.getPropertyPath().toString(), violation.getMessage()))
        );

        ValidationErrorResponse response = new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                request.getRequestURI(),
                violations
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Data integrity violation",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @Getter
    public static class ErrorResponse {
        private final LocalDateTime timestamp;
        private final int status;
        private final String error;
        private final String path;

        public ErrorResponse(LocalDateTime timestamp, int status, String error, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.path = path;
        }

    }

    @Getter
    public static class ValidationErrorResponse extends ErrorResponse {
        private final List<FieldViolation> fieldErrors;

        public ValidationErrorResponse(LocalDateTime timestamp, int status, String error, String path,
                                       List<FieldViolation> fieldErrors) {
            super(timestamp, status, error, path);
            this.fieldErrors = fieldErrors;
        }

    }

    @Getter
    public static class FieldViolation {
        private final String field;
        private final String message;

        public FieldViolation(String field, String message) {
            this.field = field;
            this.message = message;
        }

    }
}
