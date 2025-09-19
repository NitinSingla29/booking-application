package com.example.catalog.transfer.client;

import com.example.catalog.enumeration.SeatReservationStatus;
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
    private List<String> seatCodes;
    private SeatReservationStatus status;
    private String message;

    public SeatHoldResponse(SeatReservationStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}