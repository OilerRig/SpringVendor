package com.oilerrig.vendor.controller;

import com.oilerrig.vendor.data.dto.ProductResponse;
import com.oilerrig.vendor.data.dto.ReserveRequest;
import com.oilerrig.vendor.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable UUID id) {
        return productService.getProductWithDetails(id);
    }


    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@RequestBody @Valid ReserveRequest request) {
        boolean success = productService.reserveStock(request.getProductId(), request.getQuantity());
        if (!success) return ResponseEntity.badRequest().body(Map.of("error", "Insufficient stock"));
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/commit")
    public ResponseEntity<?> commit(@RequestBody @Valid ReserveRequest request) {
        productService.commitStock(request.getProductId());
        return ResponseEntity.ok(Map.of("committed", true));
    }

    @PostMapping("/revert")
    public ResponseEntity<?> revert(@RequestBody @Valid ReserveRequest request) {
        productService.revertStock(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(Map.of("reverted", true));
    }

    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("Spring Vendor API is running");
    }
}