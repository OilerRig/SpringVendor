package com.oilerrig.vendor.service;

import com.oilerrig.vendor.data.dto.OrderResponse;
import com.oilerrig.vendor.data.entities.OrderEntity;
import com.oilerrig.vendor.data.entities.ProductEntity;
import com.oilerrig.vendor.data.repository.jpa.OrderRepository;
import com.oilerrig.vendor.data.repository.jpa.ProductRepository;
import com.oilerrig.vendor.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public OrderResponse getOrder(UUID orderId) {
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return new OrderResponse(order);
    }

    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    @Transactional
    public OrderResponse placeOrder(int productId, int quantity) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (product.getStock() < quantity) throw new ResourceNotFoundException("Not enough stock");
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        OrderEntity order = new OrderEntity();
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setStatus(OrderEntity.OrderStatus.APPLIED);
        orderRepository.save(order);

        return new OrderResponse(order);
    }

    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    @Transactional
    public OrderResponse revertOrder(UUID orderId) {
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == OrderEntity.OrderStatus.APPLIED) {
            ProductEntity product = order.getProduct();
            product.setStock(product.getStock() + order.getQuantity());
            productRepository.save(product);

            order.setStatus(OrderEntity.OrderStatus.CANCELLED);
            orderRepository.save(order);
        }

        return new OrderResponse(order);
    }
}
