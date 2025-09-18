package com.example.notification.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BookingNotificationListener {

    @KafkaListener(topics = "bookings", groupId = "notification-group")
    public void onBookingEvent(String message) {
        System.out.println("NotificationService will notify users about: " + message);
    }
}
