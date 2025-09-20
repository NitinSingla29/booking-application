package com.example.payment.domain.jpa;

import com.example.core.domain.jpa.SystemCodedRelationEntity;
import com.example.core.enumeration.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Currency;

@Entity
@Table(name = "payment_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentRecord extends SystemCodedRelationEntity {

    @Column(name = "user_system_code")
    private String userSystemCode;

    @Column(name = "source_object_type")
    private String sourceObjectType;

    @Column(name = "source_object_code")
    private String sourceObjectCode;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private Currency currency;

    @Column(name = "gateway_record_id")
    private String gatewayRecordId;

    @Column(name = "payment_status")
    @Setter
    private PaymentStatus paymentStatus;


    public PaymentRecord(String userSystemCode, String sourceObjectType, String sourceObjectCode, BigDecimal amount, Currency currency, String gatewayRecordId) {
        this.userSystemCode = userSystemCode;
        this.sourceObjectType = sourceObjectType;
        this.sourceObjectCode = sourceObjectCode;
        this.amount = amount;
        this.currency = currency;
        this.gatewayRecordId = gatewayRecordId;
        this.paymentStatus = PaymentStatus.PENDING;
    }

}
