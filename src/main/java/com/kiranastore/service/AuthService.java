package com.kiranastore.service;

import com.kiranastore.dto.request.AuthLoginRequest;
import com.kiranastore.dto.request.AuthRegisterRequest;
import com.kiranastore.dto.response.AuthResponse;
import com.kiranastore.entity.User;
import com.kiranastore.entity.enums.RoleType;
import com.kiranastore.repository.UserRepository;
import com.kiranastore.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Creates the auth service.
     *
     * @param userRepository user repository
     * @param passwordEncoder password encoder
     * @param authenticationManager authentication manager
     * @param jwtService JWT service for token generation
     */
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new user and returns an auth token.
     *
     * @param request registration request
     * @return auth response with JWT
     */
    public AuthResponse register(AuthRegisterRequest request) {
        if (userRepository.existsByUserName(request.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User name already exists: " + request.getUsername()
            );
        }
        String passwordHash = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getUsername(), RoleType.CUSTOMER, passwordHash, request.getPhoneNumber());
        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser.getId());
        return new AuthResponse(token);
    }

    /**
     * Authenticates a user and returns an auth token.
     *
     * @param request login request
     * @return auth response with JWT
     */
    public AuthResponse login(AuthLoginRequest request) {
        User user = userRepository.findByUserName(request.getUsername())
                .orElseThrow(this::invalidCredentials);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getId(), request.getPassword())
            );
        } catch (AuthenticationException ex) {
            throw invalidCredentials();
        }
        String token = jwtService.generateToken(user.getId());
        return new AuthResponse(token);
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password"
        );
    }
}
