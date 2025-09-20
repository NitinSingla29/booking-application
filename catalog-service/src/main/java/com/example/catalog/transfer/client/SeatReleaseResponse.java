package com.example.catalog.transfer.client;

import com.example.core.enumeration.SeatReleaseStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SeatReleaseResponse {
    private String bookingSystemCode;
    private List<String> seatCodes;
    private SeatReleaseStatus status;
    private String message;

    public SeatReleaseResponse(SeatReleaseStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}