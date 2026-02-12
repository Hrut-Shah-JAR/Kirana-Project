package com.springlearning.kirana2.services;

import com.springlearning.kirana2.dtos.CreateTransactionItemRequest;
import com.springlearning.kirana2.dtos.CreateTransactionRequest;
import com.springlearning.kirana2.dtos.TransactionItemResponse;
import com.springlearning.kirana2.dtos.TransactionResponse;
import com.springlearning.kirana2.entity.Product;
import com.springlearning.kirana2.entity.Transaction;
import com.springlearning.kirana2.entity.TransactionItem;
import com.springlearning.kirana2.entity.User;
import com.springlearning.kirana2.entity.enums.CurrencyType;
import com.springlearning.kirana2.entity.enums.TransactionStatus;
import com.springlearning.kirana2.entity.enums.TransactionType;
import com.springlearning.kirana2.repository.ProductRepository;
import com.springlearning.kirana2.repository.TransactionItemRepository;
import com.springlearning.kirana2.repository.TransactionRepository;
import com.springlearning.kirana2.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionItemRepository transactionItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ExchangeRateService exchangeRateService;

    public Page<TransactionResponse> getTransactions(Pageable pageable) {
        if (hasRole("CUSTOMER")) {
            User currentUser = getCurrentUser();
            return transactionRepository.findByUserForeignId(currentUser.getUserId(), pageable)
                    .map(this::toResponse);
        }
        return transactionRepository.findAll(pageable).map(this::toResponse);
    }

    private TransactionResponse toResponse(Transaction entity) {
        return new TransactionResponse(
                entity.getTransactionId(),
                entity.getUserForeignId(),
                entity.getTransactionType(),
                entity.getCurrency(),
                entity.getAmountPaid(),
                entity.getAmountPaidInr(),
                entity.getTransactionStatus(),
                entity.getCreationDate(),
                entity.getOriginalTransactionId()
        );
    }

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + request.getUserId()
                ));

        BigDecimal billAmountInInr = BigDecimal.ZERO;
        CurrencyType currency = request.getCurrency();
        for (CreateTransactionItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Product not found: " + item.getProductId()
                    ));
            billAmountInInr = billAmountInInr.add(product.getPrice().multiply(item.getQuantity()));
        }

        BigDecimal amountPaid = request.getAmountPaid() != null
                ? request.getAmountPaid()
                : exchangeRateService.convertFromInr(billAmountInInr, currency);
        BigDecimal amountPaidInInr = exchangeRateService.convertToInr(amountPaid, currency);
        Transaction transaction = new Transaction(
                request.getUserId(),
                request.getTransactionType(),
                currency,
                amountPaid,
                billAmountInInr,
                TransactionStatus.Success,
                request.getOriginalTransactionId()
        );
        Transaction saved = transactionRepository.save(transaction);

        request.getItems().forEach(item -> {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Product not found: " + item.getProductId()
                    ));
            if (item.getQuantity().signum() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid quantity for product " + item.getProductId() + ": must be > 0"
                );
            }
            if (request.getTransactionType() == TransactionType.Sale &&
                    product.getQuantity().compareTo(item.getQuantity()) < 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Insufficient stock for product " + item.getProductId()
                                + " (available=" + product.getQuantity()
                                + ", requested=" + item.getQuantity() + ")"
                );
            }
            TransactionItem transactionItem = new TransactionItem(
                    saved.getTransactionId(),
                    item.getProductId(),
                    item.getQuantity(),
                    product.getPrice()
            );
            transactionItemRepository.save(transactionItem);
            adjustInventoryForTransaction(request.getTransactionType(), product, item.getQuantity());
        });

        BigDecimal balanceDelta = amountPaidInInr.subtract(billAmountInInr);
        if (balanceDelta.signum() != 0) {
            user.updateBalance(user.getBalance().add(balanceDelta));
            userRepository.save(user);
        }

        return toResponse(saved);
    }

    @Transactional
    public TransactionResponse refundTransaction(String originalTransactionId) {
        Transaction original = transactionRepository.findById(originalTransactionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transaction not found: " + originalTransactionId
                ));

        if (original.getTransactionType() == TransactionType.Refund) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot refund a refund transaction");
        }
        if (transactionRepository.existsByOriginalTransactionId(original.getTransactionId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Transaction already refunded");
        }

        Transaction refund = new Transaction(
                original.getUserForeignId(),
                TransactionType.Refund,
                original.getCurrency(),
                original.getAmountPaid(),
                original.getAmountPaidInr(),
                TransactionStatus.Success,
                original.getTransactionId()
        );
        Transaction savedRefund = transactionRepository.save(refund);

        for (TransactionItem item : transactionItemRepository.findByTransactionForeignId(original.getTransactionId())) {
            Product product = productRepository.findById(item.getProductForeignId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Product not found: " + item.getProductForeignId()
                    ));

            adjustInventoryForRefund(original.getTransactionType(), product, item.getQuantity());

            TransactionItem refundItem = new TransactionItem(
                    savedRefund.getTransactionId(),
                    item.getProductForeignId(),
                    item.getQuantity(),
                    item.getUnitPriceAtSale()
            );
            transactionItemRepository.save(refundItem);
        }

        return toResponse(savedRefund);
    }

    public Page<TransactionItemResponse> getTransactionItems(String transactionId, Pageable pageable) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transaction not found: " + transactionId
                ));
        if (hasRole("CUSTOMER")) {
            User currentUser = getCurrentUser();
            if (!transaction.getUserForeignId().equals(currentUser.getUserId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }
        }
        return transactionItemRepository.findByTransactionForeignId(transactionId, pageable)
                .map(this::toItemResponse);
    }

    private void adjustInventoryForRefund(TransactionType originalType, Product product, java.math.BigDecimal quantity) {
        if (originalType == TransactionType.Sale) {
            product.setQuantity(product.getQuantity().add(quantity));
            productRepository.save(product);
            return;
        }
        if (originalType == TransactionType.Purchase) {
            product.setQuantity(product.getQuantity().subtract(quantity));
            productRepository.save(product);
        }
    }

    private void adjustInventoryForTransaction(TransactionType transactionType, Product product,
                                               java.math.BigDecimal quantity) {
        if (transactionType == TransactionType.Sale) {
            product.setQuantity(product.getQuantity().subtract(quantity));
            productRepository.save(product);
            return;
        }
        if (transactionType == TransactionType.Purchase) {
            product.setQuantity(product.getQuantity().add(quantity));
            productRepository.save(product);
        }
    }

    private TransactionItemResponse toItemResponse(TransactionItem entity) {
        String productName = productRepository.findById(entity.getProductForeignId())
                .map(Product::getProductName)
                .orElse(null);
        return new TransactionItemResponse(
                entity.getTransactionItemId(),
                entity.getTransactionForeignId(),
                entity.getProductForeignId(),
                productName,
                entity.getQuantity(),
                entity.getUnitPriceAtSale()
        );
    }

    private boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        String authority = "ROLE_" + role;
        return authentication.getAuthorities().stream()
                .anyMatch(granted -> authority.equals(granted.getAuthority()));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        String username = authentication.getName();
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"
                ));
    }
}
