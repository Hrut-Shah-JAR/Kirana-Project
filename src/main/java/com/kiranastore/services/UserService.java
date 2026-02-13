package com.kiranastore.services;


import com.kiranastore.dtos.BalanceUpdateRequest;
import com.kiranastore.dtos.CreateUserRequest;
import com.kiranastore.dtos.UserResponse;
import com.kiranastore.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.kiranastore.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;


@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toResponse);
    }

    public UserResponse getUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + userId
                ));
        return toResponse(user);
    }

    private UserResponse toResponse(User entity) {
        return new UserResponse(
                entity.getUserId(),
                entity.getUserName(),
                entity.getRole(),
                entity.getPhoneNumber(),
                entity.getCreationDate(),
                entity.getBalance()
        );
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUserName(request.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User name already exists: " + request.getUsername()
            );
        }
        String passwordHash = passwordEncoder.encode(request.getPassword());
        User entity = new User(
                request.getUsername(),
                request.getRole(),
                passwordHash,
                request.getPhoneNumber()
        );

        User saved = userRepository.save(entity);
        return toResponse(saved);
    }

    public UserResponse updateBalance(BalanceUpdateRequest request) {
        User entity = userRepository.findByUserName(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + request.getUsername()
                ));

        entity.updateBalance(request.getBalance());

        User saved = userRepository.save(entity);
        return toResponse(saved);
    }
}
