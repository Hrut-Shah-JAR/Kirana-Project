package com.springlearning.kirana2.repository;

import com.springlearning.kirana2.entity.TransactionItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionItemRepository extends JpaRepository<TransactionItem, String> {
    List<TransactionItem> findByTransactionForeignId(String transactionForeignId);

    Page<TransactionItem> findByTransactionForeignId(String transactionForeignId, Pageable pageable);
}
