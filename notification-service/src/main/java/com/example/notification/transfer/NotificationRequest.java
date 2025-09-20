package com.example.notification.transfer;

import com.example.notification.enumeration.NotificationType;
import lombok.Data;


@Data
public class NotificationRequest {
    private String userId;
    private NotificationType type;
    private String content;
}
