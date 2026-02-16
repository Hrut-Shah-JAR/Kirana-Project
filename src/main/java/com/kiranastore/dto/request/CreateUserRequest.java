package com.kiranastore.dto.request;

import com.kiranastore.entity.enums.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateUserRequest {

    @NotBlank
    @Size(max = 100)
    private String username;

    @NotNull
    private RoleType role;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    @NotBlank
    @Size(max = 15)
    private String phoneNumber;

    public CreateUserRequest() {
    }

}
