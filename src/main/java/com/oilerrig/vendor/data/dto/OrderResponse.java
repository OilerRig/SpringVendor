package com.oilerrig.vendor.data.dto;

import com.oilerrig.vendor.data.entities.OrderEntity;

import java.time.OffsetDateTime;
import java.util.UUID;


public class OrderResponse {
    private UUID id;

    private Integer productId;
    private Integer quantity;

    private OffsetDateTime createdAt;

    private String status;

    public OrderResponse(OrderEntity orderEntity) {
        this.id = orderEntity.getId();
        this.productId = orderEntity.getProduct().getId();
        this.quantity = orderEntity.getQuantity();
        this.createdAt = orderEntity.getCreatedAt();
        this.status = orderEntity.getStatus().name();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getStatus() {
        return status;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

}

