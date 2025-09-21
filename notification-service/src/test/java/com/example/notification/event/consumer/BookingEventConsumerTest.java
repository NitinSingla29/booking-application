package com.example.notification.event.consumer;

import com.example.eventing.event.BookingConfirmedEvent;
import com.example.notification.enumeration.NotificationType;
import com.example.notification.service.INotificationService;
import com.example.notification.transfer.NotificationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BookingEventConsumerTest {

    private INotificationService notificationService;
    private BookingEventConsumer consumer;

    @BeforeEach
    void setUp() {
        notificationService = mock(INotificationService.class);
        consumer = new BookingEventConsumer(notificationService);
    }

    @Test
    void testConsumeBookingConfirmed_ShouldSendNotification() {
        // given
        BookingConfirmedEvent event = new BookingConfirmedEvent("BOOK123", "USER123", "SHOW123");

        // when
        consumer.consumeBookingConfirmed(event);

        // then
        ArgumentCaptor<NotificationRequest> captor = ArgumentCaptor.forClass(NotificationRequest.class);
        verify(notificationService, times(1)).sendNotification(captor.capture());

        NotificationRequest request = captor.getValue();
        assertThat(request.getUserSystemCode()).isEqualTo("USER123");
        assertThat(request.getType()).isEqualTo(NotificationType.BOOKING_CONFIRMED);
        assertThat(request.getContent()).isEqualTo("Your booking BOOK123 is confirmed!");
    }
}
