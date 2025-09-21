package com.example.notification.transfer;

import com.example.notification.enumeration.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String userSystemCode;
    private NotificationType type;
    private String content;
}
