package com.kiranastore.controller;

import com.kiranastore.dto.request.CreateTransactionRequest;
import com.kiranastore.dto.request.PageRequestDto;
import com.kiranastore.dto.response.PageResponseDto;
import com.kiranastore.dto.response.TransactionItemResponse;
import com.kiranastore.dto.response.TransactionResponse;
import com.kiranastore.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/v1/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAnyRole('CASHIER','MANAGER','CUSTOMER')")
    public PageResponseDto<TransactionResponse> getTransactions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return transactionService.getTransactions(new PageRequestDto(page, size));
    }

    @PostMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAnyRole('CASHIER','MANAGER')")
    public TransactionResponse createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        return transactionService.createTransaction(request);
    }

    @PostMapping(
            value = "/refund",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('MANAGER')")
    public TransactionResponse refundTransaction(@RequestParam String transactionId) {
        return transactionService.refundTransaction(transactionId);
    }

    @GetMapping(
            value = "/items",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAnyRole('CASHIER','MANAGER','CUSTOMER')")
    public PageResponseDto<TransactionItemResponse> getTransactionItems(
            @RequestParam String transactionId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return transactionService.getTransactionItems(transactionId, new PageRequestDto(page, size));
    }

}
