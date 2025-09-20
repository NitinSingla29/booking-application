package com.example.payment.transfer;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to complete payment for a booking
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletePaymentRequest {

    /**
     * Booking identifier
     */
    private String bookingSystemCode;

    /**
     * Payment details provided by the user
     */
    private PaymentDetails paymentDetails;
}

