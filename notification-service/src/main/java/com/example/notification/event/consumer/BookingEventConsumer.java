package com.example.notification.event.consumer;

import com.example.eventing.event.BookingConfirmedEvent;
import com.example.notification.enumeration.NotificationType;
import com.example.notification.service.INotificationService;
import com.example.notification.transfer.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingEventConsumer.class);

    private final INotificationService notificationService;

    @KafkaListener(topics = "booking-events", groupId = "notification-service")
    public void consumeBookingConfirmed(BookingConfirmedEvent event) {
        LOGGER.info("Received booking confirmed event: {}", event);

        String content = "Your booking " + event.getBookingSystemCode() + " is confirmed!";
        NotificationRequest request = new NotificationRequest(event.getUserSystemCode(), NotificationType.BOOKING_CONFIRMER, content);

        notificationService.sendNotification(request);
    }
}