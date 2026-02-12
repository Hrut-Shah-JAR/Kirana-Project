package com.springlearning.kirana2.controllers;

import com.springlearning.kirana2.dtos.BalanceUpdateRequest;
import com.springlearning.kirana2.dtos.CreateUserRequest;
import com.springlearning.kirana2.dtos.UserResponse;
import com.springlearning.kirana2.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/users")
@AllArgsConstructor

public class UserController {

    private final UserService userService;

    @GetMapping ("")
    public Page<UserResponse> getUsers(Pageable pageable) {
        return userService.getUsers(pageable);
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable String userId) {
        return userService.getUser(userId);
    }

    @PostMapping("")
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PatchMapping("/balance")
    public UserResponse updateBalance(@Valid @RequestBody BalanceUpdateRequest request) {
        return userService.updateBalance(request);
    }

}
