package com.example.catalog.controller;

import com.example.catalog.service.ShowPricingService;
import com.example.catalog.transfer.show.price.ShowPriceCalculationRequest;
import com.example.catalog.transfer.show.price.ShowPriceCalculationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("catalogue/pricing")
public class ShowPricingController {

    @Autowired
    private ShowPricingService showPricingService;

    @GetMapping("/show-price")
    public ShowPriceCalculationResponse calculateShowPrice(ShowPriceCalculationRequest request) {
        return showPricingService.calculateShowPrice(request);
    }
}
