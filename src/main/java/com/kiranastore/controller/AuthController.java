package com.kiranastore.controller;

import com.kiranastore.config.ApiMediaTypes;
import com.kiranastore.dto.request.AuthLoginRequest;
import com.kiranastore.dto.request.AuthRegisterRequest;
import com.kiranastore.dto.response.AuthResponse;
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

    /**
     * Creates the auth controller.
     *
     * @param authService auth service
     */
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user account and returns an auth token.
     *
     * @param request signup request payload
     * @return auth response containing JWT token
     */
    @PostMapping(
            value = "/signup",
            consumes = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON},
            produces = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON}
    )
    @PreAuthorize("permitAll()")
    public AuthResponse register(@Valid @RequestBody AuthRegisterRequest request) {
        return authService.register(request);
    }

    /**
     * Authenticates a user and returns an auth token.
     *
     * @param request login request payload
     * @return auth response containing JWT token
     */
    @PostMapping(
            value = "/login",
            consumes = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON},
            produces = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON}
    )
    @PreAuthorize("permitAll()")
    public AuthResponse login(@Valid @RequestBody AuthLoginRequest request) {
        return authService.login(request);
    }
}
