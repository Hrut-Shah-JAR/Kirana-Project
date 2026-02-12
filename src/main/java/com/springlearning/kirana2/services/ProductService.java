package com.springlearning.kirana2.services;

import com.springlearning.kirana2.dtos.CreateProductRequest;
import com.springlearning.kirana2.dtos.ProductResponse;
import com.springlearning.kirana2.entity.Product;
import com.springlearning.kirana2.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductResponse> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::toResponse);
    }

    private ProductResponse toResponse(Product entity) {
        return new ProductResponse(
                entity.getProductId(),
                entity.getProductName(),
                entity.getCategory(),
                entity.getPrice(),
                entity.getQuantity()
        );
    }

    public ProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsByProductName(request.getProductName())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Product name already exists: " + request.getProductName()
            );
        }
        Product entity = new Product(
                request.getProductName(),
                request.getCategory(),
                request.getPrice(),
                request.getQuantity()
        );

        Product saved = productRepository.save(entity);
        return toResponse(saved);
    }
}
