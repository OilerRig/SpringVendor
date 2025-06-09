package com.oilerrig.vendor.data.repository.jpa;

import com.oilerrig.vendor.data.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
}
