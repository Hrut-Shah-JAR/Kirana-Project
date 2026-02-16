package com.kiranastore.service;


import com.kiranastore.dto.BalanceUpdateRequest;
import com.kiranastore.dto.CreateUserRequest;
import com.kiranastore.dto.PageRequestDto;
import com.kiranastore.dto.PageResponseDto;
import com.kiranastore.dto.UserResponse;
import com.kiranastore.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    /**
     * Returns a paged list of users.
     *
     * @param request paging request
     * @return page response containing users
     */
    public PageResponseDto<UserResponse> getUsers(PageRequestDto request) {
        Page<UserResponse> page = userRepository
                .findAll(PageRequest.of(request.pageOrDefault(), request.sizeOrDefault()))
                .map(this::toResponse);
        return PageResponseDto.from(page);
    }

    /**
     * Returns a user by id.
     *
     * @param userId user identifier
     * @return user response
     */
    public UserResponse getUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + userId
                ));
        return toResponse(user);
    }

    /**
     * Maps a user entity to its response DTO.
     *
     * @param entity user entity
     * @return user response DTO
     */
    private UserResponse toResponse(User entity) {
        return new UserResponse(
                entity.getId(),
                entity.getUserName(),
                entity.getRole(),
                entity.getPhoneNumber(),
                entity.getCreationDate(),
                entity.getBalance()
        );
    }

    /**
     * Creates a user if the username is unique.
     *
     * @param request create user request
     * @return created user response
     */
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

    /**
     * Updates a user's balance by username.
     *
     * @param request balance update request
     * @return updated user response
     */
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
