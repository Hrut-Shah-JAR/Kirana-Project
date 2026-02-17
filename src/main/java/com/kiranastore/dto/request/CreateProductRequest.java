package com.kiranastore.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CreateProductRequest {

    @NotBlank
    @Size(max = 150)
    private String productName;

    @Size(max = 100)
    private String category;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal quantity;

    public CreateProductRequest() {
    }

}
