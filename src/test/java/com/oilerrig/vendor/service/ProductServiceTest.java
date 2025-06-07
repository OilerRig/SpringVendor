package com.oilerrig.vendor.service;

import com.oilerrig.vendor.data.dto.ProductResponse;
import com.oilerrig.vendor.exception.ResourceNotFoundException;
import com.oilerrig.vendor.data.entities.ProductEntity;
import com.oilerrig.vendor.data.entities.ProductDetailsEntity;
import com.oilerrig.vendor.data.repository.mongo.ProductDetailsRepository;
import com.oilerrig.vendor.data.repository.jpa.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepo;
    private ProductDetailsRepository detailsRepo;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepo = mock(ProductRepository.class);
        detailsRepo = mock(ProductDetailsRepository.class);
        productService = new ProductService(productRepo, detailsRepo);
    }

    @Test
    void getAllProducts_returnsList() {
        UUID id = UUID.randomUUID();
        List<ProductEntity> mockProducts = List.of(
                new ProductEntity(id, "Test", 10.0, 5)
        );
        when(productRepo.findAll()).thenReturn(mockProducts);

        List<ProductEntity> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getName());
    }

    @Test
    void getProductWithDetails_returnsMergedResponse() {
        UUID id = UUID.randomUUID();
        ProductEntity product = new ProductEntity(id, "CPU", 299.99, 10);
        Map<String, Object> specs = Map.of("cores", 8);

        when(productRepo.findById(id)).thenReturn(Optional.of(product));
        when(detailsRepo.findById(id)).thenReturn(Optional.of(new ProductDetailsEntity(id, specs)));

        ProductResponse response = productService.getProductWithDetails(id);

        assertEquals(id, response.getId());
        assertEquals("CPU", response.getName());
        assertEquals(8, response.getDetails().get("cores"));
    }

    @Test
    void getProductWithDetails_throwsIfNotFound() {
        UUID id = UUID.randomUUID();
        when(productRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductWithDetails(id);
        });
    }
}
