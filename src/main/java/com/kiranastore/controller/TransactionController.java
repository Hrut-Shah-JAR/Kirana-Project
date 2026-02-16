package com.kiranastore.controller;

import com.kiranastore.dto.CreateTransactionRequest;
import com.kiranastore.dto.PageRequestDto;
import com.kiranastore.dto.PageResponseDto;
import com.kiranastore.dto.TransactionItemResponse;
import com.kiranastore.dto.TransactionResponse;
import com.kiranastore.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/v1/api/transactions")
public class TransactionController {

    @Autowired
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private PageResponseDto<TransactionResponse> getTransactions(
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
    private TransactionResponse createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        return transactionService.createTransaction(request);
    }

    @PostMapping(
            value = "/refund",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private TransactionResponse refundTransaction(@RequestParam String transactionId) {
        return transactionService.refundTransaction(transactionId);
    }

    @GetMapping(
            value = "/items",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private PageResponseDto<TransactionItemResponse> getTransactionItems(
            @RequestParam String transactionId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return transactionService.getTransactionItems(transactionId, new PageRequestDto(page, size));
    }

}
