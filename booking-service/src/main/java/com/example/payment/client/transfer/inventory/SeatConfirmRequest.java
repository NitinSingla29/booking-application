package com.example.payment.client.transfer.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatConfirmRequest {
    private String bookingSystemCode;
}