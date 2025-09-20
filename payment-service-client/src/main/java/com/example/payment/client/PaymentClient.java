package com.example.payment.client;

import com.example.payment.transfer.PaymentRecordCreationRequest;
import com.example.payment.transfer.PaymentRecordCreationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "payment-service", url = "${services.payment.url}")
public interface PaymentClient {
    @PostMapping("/payment/record")
    PaymentRecordCreationResponse createPaymentRecord(@RequestBody PaymentRecordCreationRequest request);
}
