package com.example.booking.client;

import com.example.booking.client.transfer.payment.PaymentRecordCreationRequest;
import com.example.booking.client.transfer.payment.PaymentRecordCreationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "payment-service", url = "${services.payment.url}")
public interface PaymentClient {
    @PostMapping("/payment/record")
    PaymentRecordCreationResponse createPaymentRecord(@RequestBody PaymentRecordCreationRequest request);
}
