package com.example.payment.repository.jpa;

import com.example.core.repository.jpa.IRelationEntityRepository;
import com.example.payment.domain.jpa.PaymentRecord;

public interface IPaymentRecordRepository extends IRelationEntityRepository<PaymentRecord> {
    PaymentRecord findByGatewayRecordId(String gatewayRecordId);
}
