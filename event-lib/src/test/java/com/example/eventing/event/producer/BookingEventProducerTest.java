package com.example.eventing.event.producer;


import com.example.eventing.event.BookingConfirmedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BookingEventProducerTest {

    private KafkaTemplate<String, BookingConfirmedEvent> kafkaTemplate;
    private BookingEventProducer bookingEventProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        bookingEventProducer = new BookingEventProducer(kafkaTemplate);
    }

    @Test
    void publishBookingConfirmed_ShouldSendEventToKafka() {
        // given
        BookingConfirmedEvent event = new BookingConfirmedEvent("booking123", "user456", "show123");

        // when
        bookingEventProducer.publishBookingConfirmed(event);

        // then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BookingConfirmedEvent> eventCaptor = ArgumentCaptor.forClass(BookingConfirmedEvent.class);

        verify(kafkaTemplate, times(1)).send(
                topicCaptor.capture(),
                keyCaptor.capture(),
                eventCaptor.capture()
        );

        assertThat(topicCaptor.getValue()).isEqualTo("booking-events");
        assertThat(keyCaptor.getValue()).isEqualTo("booking123"); // bookingSystemCode is used as key
        assertThat(eventCaptor.getValue()).isEqualTo(event);
    }
}
