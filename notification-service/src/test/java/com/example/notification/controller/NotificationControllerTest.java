package com.example.notification.controller;


import com.example.notification.service.NotificationService;
import com.example.notification.transfer.NotificationRequest;
import com.example.notification.transfer.NotificationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerTest {

    private NotificationService notificationService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        notificationService = Mockito.mock(NotificationService.class);
        NotificationController controller = new NotificationController(notificationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void sendNotification_success() throws Exception {
        NotificationResponse response = new NotificationResponse();

        Mockito.when(notificationService.sendNotification(any(NotificationRequest.class)))
                .thenReturn(response);

        String requestJson = "{ \"userSystemCode\": \"user1\", \"type\": \"PAYMENT_RECEIVED\", \"content\": \"Payment received\" }";

        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void sendNotification_failure() throws Exception {
        NotificationResponse response = new NotificationResponse();
        response.fail("Some channels failed");

        Mockito.when(notificationService.sendNotification(any(NotificationRequest.class)))
                .thenReturn(response);

        String requestJson = "{ \"userSystemCode\": \"user2\", \"type\": \"BOOKING_CONFIRMED\", \"content\": \"Booking created\" }";

        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Some channels failed"));
    }
}