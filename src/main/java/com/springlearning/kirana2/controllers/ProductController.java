package com.springlearning.kirana2.controllers;

import com.springlearning.kirana2.dtos.CreateProductRequest;
import com.springlearning.kirana2.dtos.ProductResponse;
import com.springlearning.kirana2.services.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping ("")
    private Page<ProductResponse> getProducts(Pageable pageable) {
        return productService.getProducts(pageable);
    }

    @PostMapping("")
    private ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return productService.createProduct(request);
    }

}
