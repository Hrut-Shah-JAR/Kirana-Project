package com.kiranastore.controller;

import com.kiranastore.config.ApiMediaTypes;
import com.kiranastore.dto.request.CreateProductRequest;
import com.kiranastore.dto.request.PageRequestDto;
import com.kiranastore.dto.response.PageResponseDto;
import com.kiranastore.dto.response.ProductResponse;
import com.kiranastore.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/v1/api/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Creates the product controller.
     *
     * @param productService product service
     */
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Returns a paged list of products.
     *
     * @param page page index (0-based), optional
     * @param size page size, optional
     * @return paged product response
     */
    @GetMapping(
            value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON}
    )
    @PreAuthorize("isAuthenticated()")
    public PageResponseDto<ProductResponse> getProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return productService.getProducts(new PageRequestDto(page, size));
    }

    /**
     * Creates a product and returns the created resource with location header.
     *
     * @param request create product request payload
     * @return created product response
     */
    @PostMapping(
            value = "",
            consumes = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON},
            produces = {MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.V1_JSON}
    )
    @PreAuthorize("hasRole('MANAGER') or hasRole('CASHIER')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{productId}")
                        .buildAndExpand(response.getProductId())
                        .toUri()
        ).body(response);
    }

}
