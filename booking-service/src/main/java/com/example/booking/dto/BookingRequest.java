package com.example.booking.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BookingRequest {
    private UUID showId;
    private UUID userId;
    private List<String> seatNumbers;
}
