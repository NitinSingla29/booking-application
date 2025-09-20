package com.example.booking.client;

import com.example.booking.client.transfer.notification.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "notification-service", url = "${services.notification.url}")
public interface NotificationClient {
    @PostMapping("/notifications/send")
    void sendNotification(@RequestBody NotificationRequest request);
}