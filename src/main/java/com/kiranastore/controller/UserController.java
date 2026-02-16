package com.kiranastore.controller;

import com.kiranastore.config.ApiMediaTypes;
import com.kiranastore.dto.request.BalanceUpdateRequest;
import com.kiranastore.dto.request.CreateUserRequest;
import com.kiranastore.dto.request.PageRequestDto;
import com.kiranastore.dto.response.PageResponseDto;
import com.kiranastore.dto.response.UserResponse;
import com.kiranastore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping ("/v1/api/users")
public class UserController {

    private final UserService userService;

    /**
     * Creates the user controller.
     *
     * @param userService user service
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns a paged list of users.
     *
     * @param page page index (0-based), optional
     * @param size page size, optional
     * @return paged user response
     */
    @GetMapping(
            value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON}
    )
    @PreAuthorize("hasRole('MANAGER')")
    public PageResponseDto<UserResponse> getUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return userService.getUsers(new PageRequestDto(page, size));
    }

    /**
     * Returns a user by id.
     *
     * @param userId user identifier
     * @return user response
     */
    @GetMapping(
            value = "/{userId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON}
    )
    @PreAuthorize("hasRole('MANAGER') or #userId == authentication.name")
    public UserResponse getUser(@PathVariable String userId) {
        return userService.getUser(userId);
    }

    /**
     * Creates a user and returns the created resource with location header.
     *
     * @param request create user request payload
     * @return created user response
     */
    @PostMapping(
            value = "",
            consumes = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON},
            produces = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON}
    )
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{userId}")
                        .buildAndExpand(response.getUserId())
                        .toUri()
        ).body(response);
    }

    /**
     * Updates the balance for a user.
     *
     * @param userId user identifier
     * @param request balance update request payload
     * @return updated user response
     */
    @PatchMapping(
            value = "/{userId}/balance",
            consumes = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON},
            produces = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON}
    )
    @PreAuthorize("hasRole('MANAGER')")
    public UserResponse updateBalance(@PathVariable String userId, @Valid @RequestBody BalanceUpdateRequest request) {
        return userService.updateBalance(userId, request);
    }

}
