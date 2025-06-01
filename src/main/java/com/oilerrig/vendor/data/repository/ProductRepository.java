package com.oilerrig.vendor.data.repository;

import com.oilerrig.vendor.data.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
