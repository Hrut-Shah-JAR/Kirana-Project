package com.springlearning.kirana2.repository;

import com.springlearning.kirana2.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    boolean existsByOriginalTransactionId(String originalTransactionId);

    Page<Transaction> findByUserForeignId(String userForeignId, Pageable pageable);
}
