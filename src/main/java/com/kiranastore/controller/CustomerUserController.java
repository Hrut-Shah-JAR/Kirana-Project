package com.kiranastore.controller;

import com.kiranastore.config.ApiMediaTypes;
import com.kiranastore.dto.response.UserResponse;
import com.kiranastore.exception.UnauthorizedException;
import com.kiranastore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/users/me")
public class CustomerUserController {

    private final UserService userService;

    /**
     * Creates the customer user controller.
     *
     * @param userService user service
     */
    @Autowired
    public CustomerUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns the currently authenticated user's profile.
     *
     * @param authentication current authentication
     * @return authenticated user response
     */
    @GetMapping(
            value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON}
    )
    @PreAuthorize("isAuthenticated()")
    public UserResponse getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException("Authentication required");
        }
        return userService.getUser(authentication.getName());
    }
}
