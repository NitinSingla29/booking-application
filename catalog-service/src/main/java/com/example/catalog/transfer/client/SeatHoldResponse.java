package com.example.catalog.transfer.client;

import com.example.booking.enumeration.OperationStatus;
import com.example.booking.enumeration.SeatType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class SeatHoldResponse {
    private String bookingSystemCode;
    private String showSystemCode;
    private String theatreSystemCode;
    private String screenSystemCode;
    private String movieSystemCode;
    private LocalDate showDate;
    private SeatType seatType;
    private List<String> seatCodes;
    private OperationStatus status;
    private String message;

    public SeatHoldResponse(OperationStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}