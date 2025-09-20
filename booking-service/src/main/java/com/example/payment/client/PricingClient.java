package com.example.payment.client;

import com.example.payment.client.transfer.pricing.ShowPriceCalculationRequest;
import com.example.payment.client.transfer.pricing.ShowPriceCalculationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client to interact with the Pricing Service
 */
@FeignClient(name = "pricing-service", url = "${services.pricing.url}")
public interface PricingClient {

    /**
     * Calculate the booking show price based on show, seats, and user.
     *
     * @param request contains showId, seatNumbers, userId, and other pricing info
     * @return calculated price
     */
    @PostMapping("/pricing/calculate")
    ShowPriceCalculationResponse calculatePrice(@RequestBody ShowPriceCalculationRequest request);
}

