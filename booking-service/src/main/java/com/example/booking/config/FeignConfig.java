package com.example.booking.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.example")
@ConditionalOnProperty(value = "feign.client.enabled", havingValue = "true", matchIfMissing = true)
public class FeignConfig {
}
