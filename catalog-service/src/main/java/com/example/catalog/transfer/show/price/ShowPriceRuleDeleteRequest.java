package com.example.catalog.transfer.show.price;

import com.example.booking.enumeration.SeatType;
import lombok.Data;

@Data
public class ShowPriceRuleDeleteRequest {
    private String showSystemCode;
    private SeatType seatType;
}