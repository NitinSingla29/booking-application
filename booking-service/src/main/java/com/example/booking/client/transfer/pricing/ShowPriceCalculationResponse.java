package com.example.booking.client.transfer.pricing;

import com.example.core.enumeration.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Currency;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowPriceCalculationResponse {

    private String showSystemCode;

    private BigDecimal price;

    private Currency currency;

    private OperationStatus status;

    private String message;

    public ShowPriceCalculationResponse(String showSystemCode, BigDecimal price, Currency currency) {
        this.showSystemCode = showSystemCode;
        this.price = price;
        this.currency = currency;
        this.status = OperationStatus.SUCCESS;
        this.message = "Price calculated successfully";
    }

    public ShowPriceCalculationResponse(OperationStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
