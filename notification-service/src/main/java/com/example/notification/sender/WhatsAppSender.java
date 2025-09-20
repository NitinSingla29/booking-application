package com.example.notification.sender;

import com.example.notification.domain.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WhatsAppSender implements ChannelSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(WhatsAppSender.class);

    @Override
    public boolean send(Notification notification) {
        // simulate sending whatsApp
        LOGGER.info("Sending Email: " + notification.getContent());
        return true;
    }
}
