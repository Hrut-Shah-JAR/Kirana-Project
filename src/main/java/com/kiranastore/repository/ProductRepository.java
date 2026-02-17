package com.kiranastore.repository;

import com.kiranastore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {

    boolean existsByProductName(String productName);
}
