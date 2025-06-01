package com.oilerrig.vendor.service;

import com.oilerrig.vendor.data.dto.ProductResponse;
import com.oilerrig.vendor.exception.ResourceNotFoundException;
import com.oilerrig.vendor.data.entities.Product;
import com.oilerrig.vendor.data.entities.ProductDetails;
import com.oilerrig.vendor.data.repository.ProductDetailsRepository;
import com.oilerrig.vendor.data.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
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
        List<Product> mockProducts = List.of(
                new Product(id, "Test", 10.0, 5)
        );
        when(productRepo.findAll()).thenReturn(mockProducts);

        List<Product> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getName());
    }

    @Test
    void getProductWithDetails_returnsMergedResponse() {
        UUID id = UUID.randomUUID();
        Product product = new Product(id, "CPU", 299.99, 10);
        Map<String, Object> specs = Map.of("cores", 8);

        when(productRepo.findById(id)).thenReturn(Optional.of(product));
        when(detailsRepo.findById(id)).thenReturn(Optional.of(new ProductDetails(id, specs)));

        ProductResponse response = productService.getProductWithDetails(id);

        assertEquals(id, response.getId());
        assertEquals("CPU", response.getName());
        assertEquals(8, response.getDetails().get("cores"));
    }

    @Test
    void reserveStock_reducesStock_whenEnough() {
        UUID id = UUID.randomUUID();
        Product product = new Product(id, "GPU", 500.0, 5);

        when(productRepo.findById(id)).thenReturn(Optional.of(product));

        boolean result = productService.reserveStock(id, 3);

        assertTrue(result);
        assertEquals(2, product.getStock());
        verify(productRepo).save(product);
    }

    @Test
    void reserveStock_fails_whenNotEnough() {
        UUID id = UUID.randomUUID();
        Product product = new Product(id, "GPU", 500.0, 2);

        when(productRepo.findById(id)).thenReturn(Optional.of(product));

        boolean result = productService.reserveStock(id, 5);

        assertFalse(result);
        verify(productRepo, never()).save(any());
    }

    @Test
    void revertStock_addsBackStock() {
        UUID id = UUID.randomUUID();
        Product product = new Product(id, "RAM", 100.0, 10);

        when(productRepo.findById(id)).thenReturn(Optional.of(product));

        productService.revertStock(id, 3);

        assertEquals(13, product.getStock());
        verify(productRepo).save(product);
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
