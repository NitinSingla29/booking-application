package com.example.catalog.transfer.show.price;

import com.example.core.enumeration.OperationStatus;
import com.example.core.enumeration.SeatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowPriceRuleResponse {
    private String ruleSystemCode;
    private String showSystemCode;
    private SeatType seatType;
    private BigDecimal price;
    private Currency currency;
    private String message;
    private OperationStatus status;

    public ShowPriceRuleResponse(OperationStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}