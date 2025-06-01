package com.oilerrig.vendor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.oilerrig.vendor.data.repository.jpa")
public class JpaConfig {
}
