package com.kiranastore.controller;

import com.kiranastore.dto.request.BalanceUpdateRequest;
import com.kiranastore.dto.request.CreateUserRequest;
import com.kiranastore.dto.request.PageRequestDto;
import com.kiranastore.dto.response.PageResponseDto;
import com.kiranastore.dto.response.UserResponse;
import com.kiranastore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping ("/v1/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(
            value = "",
            params = "!userId",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('MANAGER')")
    public PageResponseDto<UserResponse> getUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return userService.getUsers(new PageRequestDto(page, size));
    }

    @GetMapping(
            value = "",
            params = "userId",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('MANAGER') or #userId == authentication.name")
    public UserResponse getUser(@RequestParam String userId) {
        return userService.getUser(userId);
    }


//    @PreAuthorize("hasRole('CUSTOMER')")
//    @GetMapping(
//            value = "/details/get",
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public UserResponse getUser() {
//        String userId = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
//                .map(authentication -> (UserDetails) authentication.getPrincipal())
//                .map(UserDetails::getUsername)
//                .orElse(null);
//        // validation if userId is null -> throw exception
//        return userService.getUser(userId);
//    }

    @PostMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('MANAGER')")
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PatchMapping(
            value = "/balance",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('MANAGER')")
    public UserResponse updateBalance(@Valid @RequestBody BalanceUpdateRequest request) {
        return userService.updateBalance(request);
    }

}
