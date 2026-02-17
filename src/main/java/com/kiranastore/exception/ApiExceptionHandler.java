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
                "VALIDATION_ERROR",
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
                "VALIDATION_ERROR",
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
                "DATA_INTEGRITY_VIOLATION",
                "Data integrity violation",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "CONFLICT",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "UNAUTHORIZED",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            ForbiddenException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "FORBIDDEN",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(UpstreamServiceException.class)
    public ResponseEntity<ErrorResponse> handleUpstreamServiceException(
            UpstreamServiceException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_GATEWAY.value(),
                "UPSTREAM_SERVICE_ERROR",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "Unexpected server error",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @Getter
    public static class ErrorResponse {
        private final LocalDateTime timestamp;
        private final int status;
        private final String code;
        private final String message;
        private final String path;

        public ErrorResponse(LocalDateTime timestamp, int status, String code, String message, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.code = code;
            this.message = message;
            this.path = path;
        }

    }

    @Getter
    public static class ValidationErrorResponse extends ErrorResponse {
        private final List<FieldViolation> fieldErrors;

        public ValidationErrorResponse(LocalDateTime timestamp, int status, String code, String message, String path,
                                       List<FieldViolation> fieldErrors) {
            super(timestamp, status, code, message, path);
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
