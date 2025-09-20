package com.example.notification.service;

import com.example.notification.domain.Notification;
import com.example.notification.domain.UserNotificationConfig;
import com.example.notification.enumeration.Channel;
import com.example.notification.repository.NotificationRepository;
import com.example.notification.repository.UserNotificationConfigRepository;
import com.example.notification.sender.ChannelSender;
import com.example.notification.transfer.NotificationRequest;
import com.example.notification.transfer.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class NotificationService {


    private final NotificationRepository notificationRepository;
    private final UserNotificationConfigRepository configRepository;
    private final Map<Channel, ChannelSender> channelSenders;


    public NotificationResponse sendNotification(NotificationRequest request) {
        UserNotificationConfig config = configRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("No config for user"));

        NotificationResponse notificationResponse = new NotificationResponse();

        Set<Channel> channels = config.getPreferences().get(request.getType());
        if (channels == null || channels.isEmpty()) {
            notificationResponse.fail("No channels configured for this notification type");
            return notificationResponse;
        }

        boolean overallSuccess = true;
        for (Channel channel : channels) {
            Notification notification = Notification.builder()
                    .userId(request.getUserId())
                    .type(request.getType())
                    .channel(channel)
                    .content(request.getContent())
                    .sentAt(LocalDateTime.now())
                    .build();


            boolean success = channelSenders.get(channel).send(notification);
            notification.setSuccess(success);
            notificationRepository.save(notification);
            overallSuccess &= success;
        }

        if (!overallSuccess) {
            notificationResponse.fail("Some channels failed");
        }
        return notificationResponse;
    }
}
