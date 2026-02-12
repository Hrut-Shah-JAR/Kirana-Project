package com.springlearning.kirana2.services;

import com.springlearning.kirana2.dtos.AuthLoginRequest;
import com.springlearning.kirana2.dtos.AuthRegisterRequest;
import com.springlearning.kirana2.dtos.AuthResponse;
import com.springlearning.kirana2.entity.User;
import com.springlearning.kirana2.entity.enums.RoleType;
import com.springlearning.kirana2.repository.UserRepository;
import com.springlearning.kirana2.security.JwtService;
import org.springframework.http.HttpStatus;
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

    public AuthResponse register(AuthRegisterRequest request) {
        if (userRepository.existsByUserName(request.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User name already exists: " + request.getUsername()
            );
        }
        String passwordHash = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getUsername(), RoleType.Customer, passwordHash, request.getPhoneNumber());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getUserName());
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthLoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String token = jwtService.generateToken(request.getUsername());
        return new AuthResponse(token);
    }
}
