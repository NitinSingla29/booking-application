package com.example.payment.controller;


import com.example.payment.service.PaymentService;
import com.example.payment.transfer.PaymentRecordCreationRequest;
import com.example.payment.transfer.PaymentRecordCreationResponse;
import com.example.payment.transfer.PaymentStatusUpdateRequest;
import com.example.payment.transfer.PaymentStatusUpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/status")
    public PaymentStatusUpdateResponse updatePaymentStatus(@RequestBody PaymentStatusUpdateRequest request) {
        return paymentService.updatePaymentStatus(request);
    }

    @PostMapping
    public PaymentRecordCreationResponse createPaymentRecord(@RequestBody PaymentRecordCreationRequest request) {
        return paymentService.createPaymentRecord(request);
    }
}