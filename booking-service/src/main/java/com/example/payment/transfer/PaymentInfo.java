package com.example.payment.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * DTO to represent payment information for a booking
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfo {

    /**
     * Payment record or payment intent identifier
     */
    private String paymentRecordId;

    /**
     * Amount to be paid
     */
    private BigDecimal amount;

    /**
     * Currency of the amount (e.g., INR, USD)
     */
    private Currency currency;
}
