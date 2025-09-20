package com.example.notification.controller;

import com.example.notification.service.NotificationService;
import com.example.notification.transfer.NotificationRequest;
import com.example.notification.transfer.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {


    private final NotificationService notificationService;


    @PostMapping
    public NotificationResponse send(@RequestBody NotificationRequest request) {
        return notificationService.sendNotification(request);
    }
}
