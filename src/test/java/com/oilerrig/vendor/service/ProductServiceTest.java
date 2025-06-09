package com.oilerrig.vendor.service;

import com.oilerrig.vendor.data.dto.ProductResponse;
import com.oilerrig.vendor.data.entities.ProductEntity;
import com.oilerrig.vendor.data.entities.ProductDetailsEntity;
import com.oilerrig.vendor.data.repository.jpa.ProductRepository;
import com.oilerrig.vendor.data.repository.mongo.ProductDetailsRepository;
import com.oilerrig.vendor.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductDetailsRepository detailsRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAllProducts_returnsList() {
        var p1 = new ProductEntity(1, "A", 1.0, 10);
        var p2 = new ProductEntity(2, "B", 2.0, 20);
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        var result = productService.getAllProducts();
        assertThat(result).hasSize(2).containsExactly(p1, p2);
    }

    @Test
    void getProduct_existing_withoutDetails() {
        var p = new ProductEntity(5, "X", 9.9, 5);
        when(productRepository.findById(5)).thenReturn(Optional.of(p));

        ProductResponse resp = productService.getProduct(5);
        assertThat(resp.getId()).isEqualTo(5);
        assertThat(resp.getDetails()).isNull();
    }

    @Test
    void getProduct_notFound_throws() {
        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.getProduct(1))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Product not found");
    }

    @Test
    void getProductWithDetails_existing_withDetails() {
        var p = new ProductEntity(7, "Y", 7.7, 7);
        var specs = Map.<String,Object>of("color","red");
        when(productRepository.findById(7)).thenReturn(Optional.of(p));
        when(detailsRepository.findById(7))
            .thenReturn(Optional.of(new ProductDetailsEntity(7, specs)));

        ProductResponse resp = productService.getProductWithDetails(7);
        assertThat(resp.getDetails()).containsEntry("color", "red");
    }

    @Test
    void getProductWithDetails_missingDetails_returnsEmptyMap() {
        var p = new ProductEntity(8, "Z", 8.8, 8);
        when(productRepository.findById(8)).thenReturn(Optional.of(p));
        when(detailsRepository.findById(8)).thenReturn(Optional.empty());

        ProductResponse resp = productService.getProductWithDetails(8);
        assertThat(resp.getDetails()).isEmpty();
    }
}
