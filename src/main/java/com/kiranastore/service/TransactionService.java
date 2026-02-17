package com.kiranastore.service;

import com.kiranastore.dto.request.CreateTransactionItemRequest;
import com.kiranastore.dto.request.CreateTransactionRequest;
import com.kiranastore.dto.request.PageRequestDto;
import com.kiranastore.dto.response.PageResponseDto;
import com.kiranastore.dto.response.TransactionItemResponse;
import com.kiranastore.dto.response.TransactionResponse;
import com.kiranastore.entity.Product;
import com.kiranastore.entity.Transaction;
import com.kiranastore.entity.TransactionItem;
import com.kiranastore.entity.User;
import com.kiranastore.entity.enums.CurrencyType;
import com.kiranastore.entity.enums.TransactionStatus;
import com.kiranastore.entity.enums.TransactionType;
import com.kiranastore.exception.BadRequestException;
import com.kiranastore.exception.ConflictException;
import com.kiranastore.exception.NotFoundException;
import com.kiranastore.exception.UnauthorizedException;
import com.kiranastore.repository.ProductRepository;
import com.kiranastore.repository.TransactionItemRepository;
import com.kiranastore.repository.TransactionRepository;
import com.kiranastore.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionItemRepository transactionItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ExchangeRateService exchangeRateService;
    private final AuthorizationPolicyService authorizationPolicyService;

    /**
     * Returns a paged list of transactions, filtered by user when role is CUSTOMER.
     *
     * @param request paging request
     * @return page response containing transactions
     */
    public PageResponseDto<TransactionResponse> getTransactions(PageRequestDto request, String requesterUserId,
                                                                Collection<String> requesterAuthorities) {
        PageRequest pageRequest = PageRequest.of(request.pageOrDefault(), request.sizeOrDefault());
        if (authorizationPolicyService.isCustomer(requesterAuthorities)) {
            userRepository.findById(requesterUserId)
                    .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));
            Page<TransactionResponse> page = transactionRepository
                    .findByUserForeignId(requesterUserId, pageRequest)
                    .map(this::toResponse);
            return PageResponseDto.from(page);
        }
        Page<TransactionResponse> page = transactionRepository.findAll(pageRequest).map(this::toResponse);
        return PageResponseDto.from(page);
    }

    /**
     * Maps a transaction entity to its response DTO.
     *
     * @param entity transaction entity
     * @return transaction response DTO
     */
    private TransactionResponse toResponse(Transaction entity) {
        return new TransactionResponse(
                entity.getId(),
                entity.getUserForeignId(),
                entity.getTransactionType(),
                entity.getCurrency(),
                entity.getAmountPaid(),
                entity.getTransactionStatus(),
                entity.getCreationDate(),
                entity.getOriginalTransactionId()
        );
    }

    /**
     * Creates a transaction with items, validates stock, and updates user balance.
     *
     * @param request transaction create request
     * @return created transaction response
     */
    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found: " + request.getUserId()));

        BigDecimal billAmountInInr = BigDecimal.ZERO;
        CurrencyType currency = request.getCurrency();
        for (CreateTransactionItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + item.getProductId()));
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
                TransactionStatus.SUCCESS,
                request.getOriginalTransactionId()
        );
        Transaction saved = transactionRepository.save(transaction);

        request.getItems().forEach(item -> {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + item.getProductId()));
            if (item.getQuantity().signum() <= 0) {
                throw new BadRequestException("Invalid quantity for product " + item.getProductId() + ": must be > 0");
            }
            if (request.getTransactionType() == TransactionType.SALE &&
                    product.getQuantity().compareTo(item.getQuantity()) < 0) {
                throw new BadRequestException(
                        "Insufficient stock for product " + item.getProductId()
                                + " (available=" + product.getQuantity()
                                + ", requested=" + item.getQuantity() + ")"
                );
            }
            TransactionItem transactionItem = new TransactionItem(
                    saved.getId(),
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

    /**
     * Creates a refund transaction for the given original transaction id.
     *
     * @param originalTransactionId original transaction id
     * @return refund transaction response
     */
    @Transactional
    public TransactionResponse refundTransaction(String originalTransactionId) {
        Transaction original = transactionRepository.findById(originalTransactionId)
                .orElseThrow(() -> new NotFoundException("Transaction not found: " + originalTransactionId));

        if (original.getTransactionType() == TransactionType.REFUND) {
            throw new BadRequestException("Cannot refund a refund transaction");
        }
        if (transactionRepository.existsByOriginalTransactionId(original.getId())) {
            throw new ConflictException("Transaction already refunded");
        }

        Transaction refund = new Transaction(
                original.getUserForeignId(),
                TransactionType.REFUND,
                original.getCurrency(),
                original.getAmountPaid(),
                TransactionStatus.SUCCESS,
                original.getId()
        );
        Transaction savedRefund = transactionRepository.save(refund);

        for (TransactionItem item : transactionItemRepository.findByTransactionForeignId(original.getId())) {
            Product product = productRepository.findById(item.getProductForeignId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + item.getProductForeignId()));

            adjustInventoryForRefund(original.getTransactionType(), product, item.getQuantity());

            TransactionItem refundItem = new TransactionItem(
                    savedRefund.getId(),
                    item.getProductForeignId(),
                    item.getQuantity(),
                    item.getUnitPriceAtSale()
            );
            transactionItemRepository.save(refundItem);
        }

        return toResponse(savedRefund);
    }

    /**
     * Returns a paged list of items for a transaction, with access checks for customers.
     *
     * @param transactionId transaction id
     * @param request paging request
     * @return page response containing transaction items
     */
    public PageResponseDto<TransactionItemResponse> getTransactionItems(
            String transactionId,
            PageRequestDto request,
            String requesterUserId,
            Collection<String> requesterAuthorities
    ) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction not found: " + transactionId));
        authorizationPolicyService.assertTransactionAccess(
                transaction.getUserForeignId(),
                requesterUserId,
                requesterAuthorities
        );
        Page<TransactionItemResponse> page = transactionItemRepository
                .findByTransactionForeignId(
                        transactionId,
                        PageRequest.of(request.pageOrDefault(), request.sizeOrDefault())
                )
                .map(this::toItemResponse);
        return PageResponseDto.from(page);
    }

    /**
     * Adjusts inventory for a refund based on the original transaction type.
     *
     * @param originalType original transaction type
     * @param product product entity
     * @param quantity quantity to adjust
     */
    private void adjustInventoryForRefund(TransactionType originalType, Product product, java.math.BigDecimal quantity) {
        if (originalType == TransactionType.SALE) {
            product.setQuantity(product.getQuantity().add(quantity));
            productRepository.save(product);
            return;
        }
        if (originalType == TransactionType.PURCHASE) {
            product.setQuantity(product.getQuantity().subtract(quantity));
            productRepository.save(product);
        }
    }

    /**
     * Adjusts inventory for a transaction based on its type.
     *
     * @param transactionType transaction type
     * @param product product entity
     * @param quantity quantity to adjust
     */
    private void adjustInventoryForTransaction(TransactionType transactionType, Product product,
                                               java.math.BigDecimal quantity) {
        if (transactionType == TransactionType.SALE) {
            product.setQuantity(product.getQuantity().subtract(quantity));
            productRepository.save(product);
            return;
        }
        if (transactionType == TransactionType.PURCHASE) {
            product.setQuantity(product.getQuantity().add(quantity));
            productRepository.save(product);
        }
    }

    /**
     * Maps a transaction item entity to its response DTO, including product name lookup.
     *
     * @param entity transaction item entity
     * @return transaction item response DTO
     */
    private TransactionItemResponse toItemResponse(TransactionItem entity) {
        String productName = productRepository.findById(entity.getProductForeignId())
                .map(Product::getProductName)
                .orElse(null);
        return new TransactionItemResponse(
                entity.getId(),
                entity.getTransactionForeignId(),
                entity.getProductForeignId(),
                productName,
                entity.getQuantity(),
                entity.getUnitPriceAtSale()
        );
    }
}
