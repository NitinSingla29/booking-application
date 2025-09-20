package com.example.notification.repository;

import com.example.notification.domain.UserNotificationConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface UserNotificationConfigRepository extends MongoRepository<UserNotificationConfig, String> {
    Optional<UserNotificationConfig> findByUserId(String userId);
}
