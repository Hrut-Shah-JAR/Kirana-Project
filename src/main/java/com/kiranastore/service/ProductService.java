package com.kiranastore.service;

import com.kiranastore.dto.request.CreateProductRequest;
import com.kiranastore.dto.request.PageRequestDto;
import com.kiranastore.dto.response.PageResponseDto;
import com.kiranastore.dto.response.ProductResponse;
import com.kiranastore.entity.Product;
import com.kiranastore.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Returns a paged list of products.
     *
     * @param request paging request
     * @return page response containing products
     */
    public PageResponseDto<ProductResponse> getProducts(PageRequestDto request) {
        Page<ProductResponse> page = productRepository
                .findAll(PageRequest.of(request.pageOrDefault(), request.sizeOrDefault()))
                .map(this::toResponse);
        return PageResponseDto.from(page);
    }

    /**
     * Maps a product entity to its response DTO.
     *
     * @param entity product entity
     * @return product response DTO
     */
    private ProductResponse toResponse(Product entity) {
        return new ProductResponse(
                entity.getId(),
                entity.getProductName(),
                entity.getCategory(),
                entity.getPrice(),
                entity.getQuantity()
        );
    }

    /**
     * Creates a product if the name is unique.
     *
     * @param request create product request
     * @return created product response
     */
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
