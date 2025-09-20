package com.example.catalog.transfer.show.price;

import com.example.booking.enumeration.SeatType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

@Data
public class ShowPriceRuleUpdateRequest {
    private String ruleSystemCode;
    private String showSystemCode;
    private SeatType seatType;
    private BigDecimal price;
    private Currency currency;
}