package com.example.payment.client.transfer.inventory;

import com.example.core.enumeration.OperationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SeatConfirmResponse {
    private String bookingSystemCode;
    private OperationStatus status;
    private String message;

    public SeatConfirmResponse(OperationStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}