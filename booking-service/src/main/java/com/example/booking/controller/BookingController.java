package com.example.booking.controller;

import com.example.booking.service.BookingService;
import com.example.booking.transfer.BookingRequest;
import com.example.booking.transfer.BookingResponse;
import com.example.booking.transfer.CompletePaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/start")
    public BookingResponse startBooking(@RequestBody BookingRequest request) {
        return bookingService.startBooking(request);
    }

    @PostMapping("/complete-payment")
    public BookingResponse completePayment(@RequestBody CompletePaymentRequest request) {
        return bookingService.completePayment(request);
    }
}