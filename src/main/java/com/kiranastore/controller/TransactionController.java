package com.kiranastore.controller;

import com.kiranastore.config.ApiMediaTypes;
import com.kiranastore.dto.request.CreateTransactionRequest;
import com.kiranastore.dto.request.PageRequestDto;
import com.kiranastore.dto.response.PageResponseDto;
import com.kiranastore.dto.response.TransactionItemResponse;
import com.kiranastore.dto.response.TransactionResponse;
import com.kiranastore.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping ("/v1/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Creates the transaction controller.
     *
     * @param transactionService transaction service
     */
    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Returns a paged list of transactions.
     *
     * @param page page index (0-based), optional
     * @param size page size, optional
     * @return paged transaction response
     */
    @GetMapping(
            value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON}
    )
    @PreAuthorize("hasAnyRole('CASHIER','MANAGER','CUSTOMER')")
    public PageResponseDto<TransactionResponse> getTransactions(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return transactionService.getTransactions(
                new PageRequestDto(page, size),
                authentication.getName(),
                authenticationAuthorities(authentication)
        );
    }

    /**
     * Creates a transaction and returns the created resource with location header.
     *
     * @param request create transaction request payload
     * @return created transaction response
     */
    @PostMapping(
            value = "",
            consumes = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON},
            produces = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON}
    )
    @PreAuthorize("hasAnyRole('CASHIER','MANAGER')")
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{transactionId}")
                        .buildAndExpand(response.getTransactionId())
                        .toUri()
        ).body(response);
    }

    /**
     * Creates a refund transaction for the provided transaction id.
     *
     * @param transactionId original transaction id
     * @return created refund transaction response
     */
    @PostMapping(
            value = "/{transactionId}/refund",
            produces = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON}
    )
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<TransactionResponse> refundTransaction(@PathVariable String transactionId) {
        TransactionResponse response = transactionService.refundTransaction(transactionId);
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequestUri()
                        .replacePath("/v1/api/transactions/{transactionId}")
                        .buildAndExpand(response.getTransactionId())
                        .toUri()
        ).body(response);
    }

    /**
     * Returns paged transaction items for the specified transaction.
     *
     * @param transactionId transaction id
     * @param page page index (0-based), optional
     * @param size page size, optional
     * @return paged transaction item response
     */
    @GetMapping(
            value = "/{transactionId}/items",
            produces = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON}
    )
    @PreAuthorize("hasAnyRole('CASHIER','MANAGER','CUSTOMER')")
    public PageResponseDto<TransactionItemResponse> getTransactionItems(
            Authentication authentication,
            @PathVariable String transactionId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return transactionService.getTransactionItems(
                transactionId,
                new PageRequestDto(page, size),
                authentication.getName(),
                authenticationAuthorities(authentication)
        );
    }

    private Set<String> authenticationAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toSet());
    }

}
