package com.example.notification.service;

import com.example.notification.transfer.NotificationRequest;
import com.example.notification.transfer.NotificationResponse;

public interface INotificationService {
    NotificationResponse sendNotification(NotificationRequest request);
}
