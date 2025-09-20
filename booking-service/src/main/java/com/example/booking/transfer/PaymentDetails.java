package com.example.booking.transfer;

import com.example.booking.enumeration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO representing payment details from UI / Payment Gateway
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetails {

    /**
     * Payment record ID created in step 1
     */
    private String paymentRecordId;

    /**
     * Amount paid by the user
     */
    private BigDecimal paymentAmount;

    /**
     * Status of the payment: SUCCESS / FAILED
     */
    private PaymentStatus paymentStatus;
}
