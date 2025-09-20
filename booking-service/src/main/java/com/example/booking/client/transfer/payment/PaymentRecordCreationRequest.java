package com.example.booking.client.transfer.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecordCreationRequest {
    private String userSystemCode;
    private String sourceObjectType;
    private String sourceObjectCode;
    private BigDecimal amount;
    private Currency currency;
}