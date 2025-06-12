package com.oilerrig.vendor.data.repository.jpa;

import com.oilerrig.vendor.data.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findDistinctFirstByApiKey(String apiKey);
}