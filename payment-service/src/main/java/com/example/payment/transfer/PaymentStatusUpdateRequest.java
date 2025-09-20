package com.example.payment.transfer;

import com.example.core.enumeration.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentStatusUpdateRequest {

    private String gatewayRecordId;

    private PaymentStatus status;
}
