package com.example.catalog.transfer.show;

import com.example.catalog.enumeration.SeatInventoryStatus;
import lombok.Data;

@Data
public class ShowSeatInventoryRequest {
    private String showSystemCode;
    private SeatInventoryStatus seatStatus; // optional filter
}