package com.example.catalog.transfer.client;

import com.example.core.enumeration.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatHoldRequest {
    private String showSystemCode;
    private List<String> seatCodes;
    private String bookingSystemCode;
    private String userSystemCode;
    private OperationStatus status;

}