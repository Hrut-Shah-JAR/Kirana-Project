package com.kiranastore.controllers;

import com.kiranastore.dtos.CreateTransactionRequest;
import com.kiranastore.dtos.TransactionItemResponse;
import com.kiranastore.dtos.TransactionResponse;
import com.kiranastore.services.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("transactions")
public class TransactionController {

    @Autowired
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping ("")
    private Page<TransactionResponse> getTransactions(Pageable pageable) {
        return transactionService.getTransactions(pageable);
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
            value = "/{transactionId}/refund",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private TransactionResponse refundTransaction(@PathVariable String transactionId) {
        return transactionService.refundTransaction(transactionId);
    }

    @GetMapping("/{transactionId}/items")
    private Page<TransactionItemResponse> getTransactionItems(
            @PathVariable String transactionId,
            Pageable pageable
    ) {
        return transactionService.getTransactionItems(transactionId, pageable);
    }

}
