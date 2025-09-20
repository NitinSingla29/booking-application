package com.example.payment.service;

import com.example.payment.domain.jpa.PaymentRecord;
import com.example.payment.repository.jpa.IPaymentRecordRepository;
import com.example.payment.transfer.PaymentRecordCreationRequest;
import com.example.payment.transfer.PaymentRecordCreationResponse;
import com.example.payment.transfer.PaymentStatusUpdateRequest;
import com.example.payment.transfer.PaymentStatusUpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private IPaymentRecordRepository paymentRecordRepository;

    public PaymentStatusUpdateResponse updatePaymentStatus(PaymentStatusUpdateRequest request) {
        PaymentRecord paymentRecord = paymentRecordRepository.findByGatewayRecordId(request.getGatewayRecordId());
        if (paymentRecord != null) {
            paymentRecord.setPaymentStatus(request.getStatus());
            paymentRecordRepository.save(paymentRecord);
        }
        return new PaymentStatusUpdateResponse();
    }


    public PaymentRecordCreationResponse createPaymentRecord(PaymentRecordCreationRequest request) {
        String gatewayRecordId = createPaymentTransactionOnGateway();

        PaymentRecord paymentRecord = new PaymentRecord(request.getUserSystemCode(),
                request.getSourceObjectType(), request.getSourceObjectCode(), request.getAmount(), request.getCurrency(), gatewayRecordId);

        var savedRecord = paymentRecordRepository.save(paymentRecord);

        return getPaymentRecordCreatedSuccessfully(savedRecord);
    }

    private static String createPaymentTransactionOnGateway() {
        // Simulated gateway record ID
        return "RAZORPAY-" + System.currentTimeMillis();
    }

    private static PaymentRecordCreationResponse getPaymentRecordCreatedSuccessfully(PaymentRecord savedRecord) {
        return new PaymentRecordCreationResponse(
                savedRecord.getUserSystemCode(),
                savedRecord.getSourceObjectType(),
                savedRecord.getSourceObjectCode(),
                savedRecord.getSystemCode(),
                savedRecord.getAmount(),
                savedRecord.getCurrency(),
                savedRecord.getGatewayRecordId()
        );
    }
}
