package com.example.notification.domain;

import com.example.notification.enumeration.Channel;
import com.example.notification.enumeration.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_notification_config")
public class UserNotificationConfig {
    @Id
    private String id;
    private String userId;
    // For each notification type, store allowed channels
    private Map<NotificationType, Set<Channel>> preferences;
}
