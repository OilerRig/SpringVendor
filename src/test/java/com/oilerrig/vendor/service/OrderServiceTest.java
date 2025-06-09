package com.oilerrig.vendor.service;

import com.oilerrig.vendor.data.dto.OrderResponse;
import com.oilerrig.vendor.data.entities.OrderEntity;
import com.oilerrig.vendor.data.entities.ProductEntity;
import com.oilerrig.vendor.data.repository.jpa.OrderRepository;
import com.oilerrig.vendor.data.repository.jpa.ProductRepository;
import com.oilerrig.vendor.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private ProductRepository prodRepo;
    @Mock private OrderRepository orderRepo;
    @InjectMocks private OrderService orderService;

    @Test
    void placeOrder_success() {
        var prod = new ProductEntity(1,"A",1.0,10);
        when(prodRepo.findById(1)).thenReturn(Optional.of(prod));

        var resp = orderService.placeOrder(1, 4);
        assertThat(resp.getQuantity()).isEqualTo(4);
        assertThat(prod.getStock()).isEqualTo(6);
        verify(orderRepo).save(any(OrderEntity.class));
    }

    @Test
    void placeOrder_insufficientStock_throws() {
        var prod = new ProductEntity(2,"B",2.0,1);
        when(prodRepo.findById(2)).thenReturn(Optional.of(prod));
        assertThatThrownBy(() -> orderService.placeOrder(2, 5))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Not enough stock");
    }

    @Test
    void getOrder_existing() {
        var id = UUID.randomUUID();
        var order = new OrderEntity();
        var prod = new ProductEntity(1, "P", 1.0, 2);
        order.setId(id);
        order.setProduct(prod);
        order.setQuantity(1);
        order.setStatus(OrderEntity.OrderStatus.APPLIED);
        when(orderRepo.findById(id)).thenReturn(Optional.of(order));

        OrderResponse resp = orderService.getOrder(id);
        assertThat(resp.getId()).isEqualTo(id);
    }

    @Test
    void revertOrder_applied() {
        var id = UUID.randomUUID();
        var prod = new ProductEntity(3,"C",3.0,5);
        var order = new OrderEntity();
        order.setId(id);
        order.setQuantity(2);
        order.setProduct(prod);
        order.setStatus(OrderEntity.OrderStatus.APPLIED);
        when(orderRepo.findById(id)).thenReturn(Optional.of(order));

        OrderResponse resp = orderService.revertOrder(id);
        assertThat(resp.getStatus()).isEqualTo("CANCELLED");
        assertThat(prod.getStock()).isEqualTo(7);
    }
}
