package com.kiranastore.controller;

import com.kiranastore.dto.BalanceUpdateRequest;
import com.kiranastore.dto.CreateUserRequest;
import com.kiranastore.dto.PageRequestDto;
import com.kiranastore.dto.PageResponseDto;
import com.kiranastore.dto.UserResponse;
import com.kiranastore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/v1/api/users")
public class UserController {

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PageResponseDto<UserResponse> getUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return userService.getUsers(new PageRequestDto(page, size));
    }

    @GetMapping(
            value = "/details",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserResponse getUser(@RequestParam String userId) {
        return userService.getUser(userId);
    }

    @PostMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PatchMapping(
            value = "/balance",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserResponse updateBalance(@Valid @RequestBody BalanceUpdateRequest request) {
        return userService.updateBalance(request);
    }

}
