package com.example.eventing.event.producer;


import com.example.eventing.event.BookingConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingEventProducer.class);
    private static final String TOPIC = "booking-events";

    private final KafkaTemplate<String, BookingConfirmedEvent> kafkaTemplate;

    public void publishBookingConfirmed(BookingConfirmedEvent event) {
        LOGGER.info("Publishing booking confirmed event: {}", event);
        kafkaTemplate.send(TOPIC, event.getBookingSystemCode(), event);
    }
}