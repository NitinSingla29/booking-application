package com.example.booking.client.transfer.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatReleaseRequest {
    private String bookingSystemCode;
}