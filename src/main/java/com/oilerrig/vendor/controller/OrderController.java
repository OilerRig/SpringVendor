package com.oilerrig.vendor.controller;

import com.oilerrig.vendor.data.dto.OrderResponse;
import com.oilerrig.vendor.data.entities.OrderEntity;
import com.oilerrig.vendor.service.OrderService;
import com.oilerrig.vendor.data.dto.OrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody @Valid OrderRequest request) {
        return ResponseEntity.ok().body(orderService.placeOrder(request.getProductId(), request.getQuantity()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok().body(orderService.getOrder(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID id) {
        return ResponseEntity.ok().body(orderService.revertOrder(id));
    }
}
