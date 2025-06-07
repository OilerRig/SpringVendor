package com.oilerrig.vendor.service;

import com.oilerrig.vendor.data.entities.ProductEntity;
import com.oilerrig.vendor.data.repository.jpa.ProductRepository;
import com.oilerrig.vendor.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private ProductRepository productRepo;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        productRepo = mock(ProductRepository.class);
        orderService = new OrderService(productRepo);
    }

    @Test
    void reserveStock_reducesStock_whenEnough() {
        UUID id = UUID.randomUUID();
        ProductEntity product = new ProductEntity(id, "GPU", 500.0, 5);
        when(productRepo.findById(id)).thenReturn(Optional.of(product));

        boolean result = orderService.reserveStock(id, 3);

        assertTrue(result);
        assertEquals(2, product.getStock());
        verify(productRepo).save(product);
    }

    @Test
    void reserveStock_fails_whenNotEnough() {
        UUID id = UUID.randomUUID();
        ProductEntity product = new ProductEntity(id, "GPU", 500.0, 2);
        when(productRepo.findById(id)).thenReturn(Optional.of(product));

        boolean result = orderService.reserveStock(id, 5);

        assertFalse(result);
        verify(productRepo, never()).save(any());
    }

    @Test
    void revertStock_addsBackStock() {
        UUID id = UUID.randomUUID();
        ProductEntity product = new ProductEntity(id, "RAM", 100.0, 10);
        when(productRepo.findById(id)).thenReturn(Optional.of(product));

        orderService.revertStock(id, 3);

        assertEquals(13, product.getStock());
        verify(productRepo).save(product);
    }

    @Test
    void reserveStock_throwsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(productRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.reserveStock(id, 1);
        });
    }
}
