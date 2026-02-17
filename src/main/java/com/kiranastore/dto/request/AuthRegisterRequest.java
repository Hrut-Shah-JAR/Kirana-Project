package com.kiranastore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthRegisterRequest {

    @NotBlank
    @Size(max = 100)
    private String username;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    @NotBlank
    @Size(max = 15)
    private String phoneNumber;

    public AuthRegisterRequest() {
    }

}
