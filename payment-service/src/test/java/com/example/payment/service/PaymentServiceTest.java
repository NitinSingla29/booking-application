package com.example.payment.service;

import com.example.common.enumeration.PaymentStatus;
import com.example.payment.BaseTest;
import com.example.payment.domain.jpa.PaymentRecord;
import com.example.payment.repository.jpa.IPaymentRecordRepository;
import com.example.payment.transfer.PaymentRecordCreationRequest;
import com.example.payment.transfer.PaymentRecordCreationResponse;
import com.example.payment.transfer.PaymentStatusUpdateRequest;
import com.example.payment.transfer.PaymentStatusUpdateResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceTest extends BaseTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private IPaymentRecordRepository paymentRecordRepository;

    @AfterEach
    void tearDown() {
        paymentRecordRepository.deleteAll();
    }

    @Test
    void testUpdatePaymentStatus_RecordExists_StatusUpdated() {
        PaymentRecord paymentRecord = new PaymentRecord(
                "USER1", "ORDER", "ORDER123", new BigDecimal("100.00"), Currency.getInstance("USD"), "GATEWAY-123"
        );
        paymentRecord.setPaymentStatus(PaymentStatus.PENDING);
        paymentRecordRepository.save(paymentRecord);

        PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest();
        request.setGatewayRecordId("GATEWAY-123");
        request.setStatus(PaymentStatus.SUCCESS);

        PaymentStatusUpdateResponse response = paymentService.updatePaymentStatus(request);

        PaymentRecord updatedRecord = paymentRecordRepository.findByGatewayRecordId("GATEWAY-123");
        assertNotNull(response);
        assertEquals(PaymentStatus.SUCCESS, updatedRecord.getPaymentStatus());
    }

    @Test
    void testUpdatePaymentStatus_RecordNotFound_NoUpdate() {
        PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest();
        request.setGatewayRecordId("GATEWAY-999");
        request.setStatus(PaymentStatus.FAILED);

        PaymentStatusUpdateResponse response = paymentService.updatePaymentStatus(request);

        assertNotNull(response);
        assertNull(paymentRecordRepository.findByGatewayRecordId("GATEWAY-999"));
    }

    @Test
    void testCreatePaymentRecord_Success() {
        PaymentRecordCreationRequest request = new PaymentRecordCreationRequest(
                "USER1", "ORDER", "ORDER123", new BigDecimal("100.00"), Currency.getInstance("USD")
        );

        PaymentRecordCreationResponse response = paymentService.createPaymentRecord(request);

        assertNotNull(response);
        assertEquals("USER1", response.getUserSystemCode());
        assertEquals("ORDER", response.getSourceObjectType());
        assertEquals("ORDER123", response.getSourceObjectCode());
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertEquals(Currency.getInstance("USD"), response.getCurrency());
        assertNotNull(response.getGatewayRecordId());

        PaymentRecord savedRecord = paymentRecordRepository.findByGatewayRecordId(response.getGatewayRecordId());
        assertNotNull(savedRecord);
        assertEquals("USER1", savedRecord.getUserSystemCode());
        assertEquals("ORDER", savedRecord.getSourceObjectType());
        assertEquals("ORDER123", savedRecord.getSourceObjectCode());
        assertEquals(new BigDecimal("100.00"), savedRecord.getAmount());
        assertEquals(Currency.getInstance("USD"), savedRecord.getCurrency());
    }

    @Test
    void testCreatePaymentRecord_PersistsRecord() {
        PaymentRecordCreationRequest request = new PaymentRecordCreationRequest(
                "USER2", "INVOICE", "INV456", new BigDecimal("250.50"), Currency.getInstance("EUR")
        );

        PaymentRecordCreationResponse response = paymentService.createPaymentRecord(request);

        PaymentRecord persisted = paymentRecordRepository.findByGatewayRecordId(response.getGatewayRecordId());
        assertNotNull(persisted);
        assertEquals("USER2", persisted.getUserSystemCode());
        assertEquals("INVOICE", persisted.getSourceObjectType());
        assertEquals("INV456", persisted.getSourceObjectCode());
        assertEquals(new BigDecimal("250.50"), persisted.getAmount());
        assertEquals(Currency.getInstance("EUR"), persisted.getCurrency());
    }

}
