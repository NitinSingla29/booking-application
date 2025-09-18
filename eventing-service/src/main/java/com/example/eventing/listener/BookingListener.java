package com.example.eventing.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BookingListener {

    @KafkaListener(topics = "bookings", groupId = "eventing-group")
    public void onBookingEvent(String message) {
        System.out.println("EventingService received: " + message);
    }
}
