package com.example.payment.transfer;

import com.example.core.transfer.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Currency;

@Getter
@Setter
@NoArgsConstructor
public class PaymentRecordCreationResponse extends Response {
    private String userSystemCode;
    private String sourceObjectType;
    private String sourceObjectCode;
    private String systemCode;
    private BigDecimal amount;
    private Currency currency;
    private String gatewayRecordId;

    public PaymentRecordCreationResponse(String userSystemCode, String sourceObjectType, String sourceObjectCode,
                                         String systemCode, BigDecimal amount, Currency currency, String gatewayRecordId) {
        this.userSystemCode = userSystemCode;
        this.sourceObjectType = sourceObjectType;
        this.sourceObjectCode = sourceObjectCode;
        this.systemCode = systemCode;
        this.amount = amount;
        this.currency = currency;
        this.gatewayRecordId = gatewayRecordId;
    }
}