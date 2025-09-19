package com.example.catalog.transfer.show.price;

import com.example.catalog.enumeration.SeatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowPriceRuleResponse {
    private String showSystemCode;
    private SeatType seatType;
    private BigDecimal price;
    private Currency currency;
    private String message;

    public ShowPriceRuleResponse(String message) {
        
        this.message = message;
    }
}