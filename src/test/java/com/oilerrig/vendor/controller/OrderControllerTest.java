package com.oilerrig.vendor.controller;

import com.oilerrig.vendor.data.dto.OrderResponse;
import com.oilerrig.vendor.data.entities.OrderEntity;
import com.oilerrig.vendor.data.entities.ProductEntity;
import com.oilerrig.vendor.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired private MockMvc mvc;
    @MockitoBean private OrderService service;

    @Test
    void placeOrder_endpoint() throws Exception {
        var order = new OrderEntity();
        order.setId(UUID.randomUUID());
        var product = new ProductEntity();
        product.setId(1);
        order.setProduct(product);
        order.setQuantity(2);
        order.setStatus(OrderEntity.OrderStatus.APPLIED);
        order.setCreatedAt(OffsetDateTime.now());
        var resp = new OrderResponse(order);

        when(service.placeOrder(1,2)).thenReturn(resp);

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":1,\"quantity\":2}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    void cancelOrder_endpoint() throws Exception {
        var id = UUID.randomUUID();
        var order = new OrderEntity();
        order.setId(id);
        var product = new ProductEntity();
        product.setId(3);
        order.setProduct(product);
        order.setQuantity(1);
        order.setStatus(OrderEntity.OrderStatus.CANCELLED);
        order.setCreatedAt(OffsetDateTime.now());
        var resp = new OrderResponse(order);

        when(service.revertOrder(id)).thenReturn(resp);

        mvc.perform(delete("/orders/" + id))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
