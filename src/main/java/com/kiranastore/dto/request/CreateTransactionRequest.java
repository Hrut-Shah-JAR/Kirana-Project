package com.kiranastore.dto.request;

import com.kiranastore.entity.enums.CurrencyType;
import com.kiranastore.entity.enums.TransactionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class CreateTransactionRequest {

    @NotBlank
    @Size(max = 36)
    private String userId;

    @NotNull
    private TransactionType transactionType;

    @NotNull
    private CurrencyType currency;

    @DecimalMin("0.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amountPaid;

    @Size(max = 36)
    private String originalTransactionId;

    @NotEmpty
    @Valid
    private List<CreateTransactionItemRequest> items;

    public CreateTransactionRequest() {
    }

}
