package com.example.catalog.transfer.show.price;

import com.example.common.enumeration.SeatType;
import lombok.Data;

@Data
public class ShowPriceRuleDeleteRequest {
    private String showSystemCode;
    private SeatType seatType;
}