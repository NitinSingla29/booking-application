package com.example.notification.sender;

import com.example.notification.domain.Notification;


public interface ChannelSender {
    boolean send(Notification notification);
}