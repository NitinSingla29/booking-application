package com.example.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookingResponse {
    private boolean success;
    private String message;
    private List<String> seats;
}
