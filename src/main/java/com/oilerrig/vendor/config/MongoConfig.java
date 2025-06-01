package com.oilerrig.vendor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.oilerrig.vendor.data.repository.mongo")
public class MongoConfig {
}
