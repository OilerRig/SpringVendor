package com.oilerrig.vendor.data.repository;

import com.oilerrig.vendor.data.entities.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductDetailsRepository extends JpaRepository<ProductDetails, UUID> {
}
