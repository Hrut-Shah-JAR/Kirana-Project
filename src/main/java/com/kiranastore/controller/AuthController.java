package com.kiranastore.controller;

import com.kiranastore.dto.AuthLoginRequest;
import com.kiranastore.dto.AuthRegisterRequest;
import com.kiranastore.dto.AuthResponse;
import com.kiranastore.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(
            value = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("permitAll()")
    public AuthResponse register(@Valid @RequestBody AuthRegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("permitAll()")
    public AuthResponse login(@Valid @RequestBody AuthLoginRequest request) {
        return authService.login(request);
    }
}
