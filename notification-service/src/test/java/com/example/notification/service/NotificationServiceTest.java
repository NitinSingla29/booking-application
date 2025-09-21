package com.example.notification.service;


import com.example.notification.domain.Notification;
import com.example.notification.domain.UserNotificationConfig;
import com.example.notification.enumeration.Channel;
import com.example.notification.enumeration.NotificationType;
import com.example.notification.repository.NotificationRepository;
import com.example.notification.repository.UserNotificationConfigRepository;
import com.example.notification.sender.ChannelSender;
import com.example.notification.transfer.NotificationRequest;
import com.example.notification.transfer.NotificationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationRepository notificationRepository;
    private UserNotificationConfigRepository configRepository;
    private ChannelSender emailSender;
    private ChannelSender whatsappSender;
    private Map<Channel, ChannelSender> channelSenders;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        configRepository = mock(UserNotificationConfigRepository.class);
        emailSender = mock(ChannelSender.class);
        whatsappSender = mock(ChannelSender.class);
        channelSenders = new HashMap<>();
        channelSenders.put(Channel.EMAIL, emailSender);
        channelSenders.put(Channel.WHATSAPP, whatsappSender);

        notificationService = new NotificationService(notificationRepository, configRepository, channelSenders);
    }

    @Test
    void sendNotification_success() {
        NotificationRequest request = new NotificationRequest();
        request.setUserSystemCode("user1");
        request.setType(NotificationType.PAYMENT_RECEIVED);
        request.setContent("Payment received message");

        Set<Channel> channels = new HashSet<>(Arrays.asList(Channel.EMAIL, Channel.WHATSAPP));
        Map<NotificationType, Set<Channel>> prefs = new HashMap<>();
        prefs.put(NotificationType.PAYMENT_RECEIVED, channels);

        UserNotificationConfig config = new UserNotificationConfig();
        config.setUserId("user1");
        config.setPreferences(prefs);

        when(configRepository.findByUserId("user1")).thenReturn(Optional.of(config));
        when(emailSender.send(any())).thenReturn(true);
        when(whatsappSender.send(any())).thenReturn(true);

        NotificationResponse response = notificationService.sendNotification(request);

        assertTrue(response.isSuccess());
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void sendNotification_noConfig() {
        NotificationRequest request = new NotificationRequest();
        request.setUserSystemCode("user2");
        request.setType(NotificationType.BOOKING_CONFIRMER);
        request.setContent("Booking created message");

        when(configRepository.findByUserId("user2")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            notificationService.sendNotification(request);
        });
        assertEquals("No config for user", ex.getMessage());
    }

    @Test
    void sendNotification_noChannelsConfigured() {
        NotificationRequest request = new NotificationRequest();
        request.setUserSystemCode("user3");
        request.setType(NotificationType.BOOKING_CONFIRMER);
        request.setContent("Booking created message");

        Map<NotificationType, Set<Channel>> prefs = new HashMap<>();
        prefs.put(NotificationType.BOOKING_CONFIRMER, Collections.emptySet());

        UserNotificationConfig config = new UserNotificationConfig();
        config.setUserId("user3");
        config.setPreferences(prefs);

        when(configRepository.findByUserId("user3")).thenReturn(Optional.of(config));

        NotificationResponse response = notificationService.sendNotification(request);

        assertFalse(response.isSuccess());
        assertEquals("No channels configured for this notification type", response.getMessage());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void sendNotification_partialFailure() {
        NotificationRequest request = new NotificationRequest();
        request.setUserSystemCode("user4");
        request.setType(NotificationType.PAYMENT_RECEIVED);
        request.setContent("Payment received message");

        Set<Channel> channels = new HashSet<>(Arrays.asList(Channel.EMAIL, Channel.WHATSAPP));
        Map<NotificationType, Set<Channel>> prefs = new HashMap<>();
        prefs.put(NotificationType.PAYMENT_RECEIVED, channels);

        UserNotificationConfig config = new UserNotificationConfig();
        config.setUserId("user4");
        config.setPreferences(prefs);

        when(configRepository.findByUserId("user4")).thenReturn(Optional.of(config));
        when(emailSender.send(any())).thenReturn(true);
        when(whatsappSender.send(any())).thenReturn(false);

        NotificationResponse response = notificationService.sendNotification(request);

        assertFalse(response.isSuccess());
        assertEquals("Some channels failed", response.getMessage());
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }
}