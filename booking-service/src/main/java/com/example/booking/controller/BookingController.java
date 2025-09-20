package com.example.booking.controller;

import com.example.booking.service.BookingService;
import com.example.booking.transfer.BookingRequest;
import com.example.booking.transfer.BookingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> book(@RequestBody BookingRequest request) {
        BookingResponse resp = bookingService.startBooking(request);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> get(@PathVariable UUID id) {
        return ResponseEntity.ok("Not implemented");
    }
}
