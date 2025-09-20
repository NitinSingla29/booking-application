package com.example.catalog;

import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@TestConfiguration
public abstract class BaseTest {

    @Bean
    public RedissonClient redisson() {
        return Mockito.mock(RedissonClient.class);
    }
}