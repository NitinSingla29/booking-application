package com.example.booking.client.transfer.payment;

import com.example.booking.enumeration.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecordCreationResponse {
    private String userSystemCode;
    private String sourceObjectType;
    private String sourceObjectCode;
    private String systemCode;
    private BigDecimal amount;
    private Currency currency;
    private OperationStatus status;
    private String message;
}