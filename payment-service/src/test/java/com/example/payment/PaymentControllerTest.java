package com.example.payment;

import com.example.common.enumeration.PaymentStatus;
import com.example.payment.domain.jpa.PaymentRecord;
import com.example.payment.repository.jpa.IPaymentRecordRepository;
import com.example.payment.transfer.PaymentRecordCreationRequest;
import com.example.payment.transfer.PaymentStatusUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class PaymentControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IPaymentRecordRepository paymentRecordRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        paymentRecordRepository.deleteAll();
    }

    @Test
    void testCreatePaymentRecord_Success() throws Exception {
        PaymentRecordCreationRequest request = new PaymentRecordCreationRequest(
                "USER1", "ORDER", "ORDER123", new BigDecimal("100.00"), Currency.getInstance("USD")
        );

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userSystemCode").value("USER1"))
                .andExpect(jsonPath("$.sourceObjectType").value("ORDER"))
                .andExpect(jsonPath("$.sourceObjectCode").value("ORDER123"))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.gatewayRecordId").isNotEmpty());

        // Verify persistence
        assertThat(paymentRecordRepository.findAll()).hasSize(1);
    }

    @Test
    void testUpdatePaymentStatus_RecordExists_StatusUpdated() throws Exception {
        PaymentRecord paymentRecord = new PaymentRecord(
                "USER1", "ORDER", "ORDER123", new BigDecimal("100.00"), Currency.getInstance("USD"), "GATEWAY-123"
        );
        paymentRecord.setPaymentStatus(PaymentStatus.PENDING);
        paymentRecordRepository.save(paymentRecord);

        PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest();
        request.setGatewayRecordId("GATEWAY-123");
        request.setStatus(PaymentStatus.SUCCESS);

        mockMvc.perform(post("/payments/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        PaymentRecord updated = paymentRecordRepository.findByGatewayRecordId("GATEWAY-123");
        assertThat(updated.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void testUpdatePaymentStatus_RecordNotFound() throws Exception {
        PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest();
        request.setGatewayRecordId("NON_EXISTENT");
        request.setStatus(PaymentStatus.FAILED);

        mockMvc.perform(post("/payments/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertThat(paymentRecordRepository.findByGatewayRecordId("NON_EXISTENT")).isNull();
    }
}
