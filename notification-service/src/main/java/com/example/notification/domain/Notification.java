package com.example.notification.domain;

import com.example.notification.enumeration.Channel;
import com.example.notification.enumeration.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    private String userId;
    private NotificationType type;
    private Channel channel;
    private String content;
    private LocalDateTime sentAt;
    private boolean success;
}
