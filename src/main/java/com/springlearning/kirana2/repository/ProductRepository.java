package com.springlearning.kirana2.repository;

import com.springlearning.kirana2.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {

    boolean existsByProductName(String productName);
}
