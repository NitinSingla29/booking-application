package com.example.eventing.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingConfirmedEvent {
    private String bookingSystemCode;
    private String userSystemCode;
    private String showSystemCode;
}

