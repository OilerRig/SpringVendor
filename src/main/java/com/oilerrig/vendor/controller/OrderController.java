package com.oilerrig.vendor.controller;

import com.oilerrig.vendor.service.OrderService;
import com.oilerrig.vendor.data.dto.OrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody @Valid OrderRequest request) {
        boolean reserved = orderService.reserveStock(request.getProductId(), request.getQuantity());
        if (!reserved) {
            return ResponseEntity.badRequest().body("Insufficient stock");
        }
        return ResponseEntity.ok().body("Stock reserved");
    }

    @DeleteMapping
    public ResponseEntity<?> cancelOrder(@RequestBody @Valid OrderRequest request) {
        orderService.revertStock(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok().body("Stock reverted");
    }
}
