package com.example.catalog.transfer.show.price;

import com.example.core.enumeration.SeatType;
import lombok.Data;

@Data
public class ShowPriceRuleDeleteRequest {
    private String showSystemCode;
    private SeatType seatType;
}