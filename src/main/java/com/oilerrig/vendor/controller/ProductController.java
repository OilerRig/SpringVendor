package com.oilerrig.vendor.controller;

import com.oilerrig.vendor.data.dto.ProductResponse;
import com.oilerrig.vendor.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<?>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}/details")
    public ProductResponse getProductWithDetails(@PathVariable Integer id) {
        return productService.getProductWithDetails(id);
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Integer id) {
        return productService.getProduct(id);
    }

}


