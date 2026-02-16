package com.kiranastore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthLoginRequest {

    @NotBlank
    @Size(max = 100)
    private String username;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    public AuthLoginRequest() {
    }

}
