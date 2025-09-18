package com.example.inventory.transfer.show;

import com.example.inventory.enumeration.SeatInventoryStatus;
import lombok.Data;

@Data
public class ShowSeatInventoryRequest {
    private String showSystemCode;
    private SeatInventoryStatus seatStatus; // optional filter
}