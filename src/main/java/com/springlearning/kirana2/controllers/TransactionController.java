package com.springlearning.kirana2.controllers;

import com.springlearning.kirana2.dtos.CreateTransactionRequest;
import com.springlearning.kirana2.dtos.TransactionItemResponse;
import com.springlearning.kirana2.dtos.TransactionResponse;
import com.springlearning.kirana2.services.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping ("transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping ()
    private Page<TransactionResponse> getTransactions(Pageable pageable) {
        return transactionService.getTransactions(pageable);
    }

    @PostMapping("")
    private TransactionResponse createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        return transactionService.createTransaction(request);
    }

    @PostMapping("/{transactionId}/refund")
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
