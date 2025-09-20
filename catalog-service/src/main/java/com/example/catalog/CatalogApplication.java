package com.example.catalog;

import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

// Exclude RedissonAutoConfigurationV2 if not using Redisson to avoid unnecessary configuration
@SpringBootApplication(exclude = {RedissonAutoConfigurationV2.class})
@EntityScan(basePackages = {"com.example.catalog.domain.jpa"})
public class CatalogApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatalogApplication.class, args);
    }
}
