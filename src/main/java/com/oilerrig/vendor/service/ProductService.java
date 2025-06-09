package com.oilerrig.vendor.service;

import com.oilerrig.vendor.data.dto.ProductResponse;
import com.oilerrig.vendor.exception.ResourceNotFoundException;
import com.oilerrig.vendor.data.entities.ProductEntity;
import com.oilerrig.vendor.data.entities.ProductDetailsEntity;
import com.oilerrig.vendor.data.repository.mongo.ProductDetailsRepository;
import com.oilerrig.vendor.data.repository.jpa.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailsRepository productDetailsRepository;

    public ProductService(ProductRepository productRepository, ProductDetailsRepository productDetailsRepository) {
        this.productRepository = productRepository;
        this.productDetailsRepository = productDetailsRepository;
    }

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    public ProductResponse getProductWithDetails(Integer id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        var details = productDetailsRepository.findById(id)
                .map(ProductDetailsEntity::getSpecs).orElse(Map.of());

        var response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setDetails(details);
        return response;
    }

    public ProductResponse getProduct(Integer id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        var response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setDetails(null);
        return response;
    }

}
