package com.oilerrig.vendor.data.repository.mongo;

import com.oilerrig.vendor.data.entities.ProductDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductDetailsRepository extends MongoRepository<ProductDetailsEntity, Integer> {
}
