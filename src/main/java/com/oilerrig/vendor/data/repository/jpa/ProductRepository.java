package com.oilerrig.vendor.data.repository.jpa;

import com.oilerrig.vendor.data.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
}
