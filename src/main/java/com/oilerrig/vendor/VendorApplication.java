package com.oilerrig.vendor;

import com.oilerrig.vendor.data.repository.ProductDetailsRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@EnableJpaRepositories(
    basePackages = "com.oilerrig.vendor.data.repository",
    excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProductDetailsRepository.class)
)
@EnableMongoRepositories(
    basePackages = "com.oilerrig.vendor.data.repository",
    includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProductDetailsRepository.class)
)
public class VendorApplication {

    public static void main(String[] args) {
        SpringApplication.run(VendorApplication.class, args);
    }
}
