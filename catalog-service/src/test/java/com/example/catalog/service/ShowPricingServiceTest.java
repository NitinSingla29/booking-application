package com.example.catalog.service;


import com.example.catalog.BaseTest;
import com.example.catalog.domain.jpa.ShowPriceRule;
import com.example.catalog.repository.jpa.IShowPriceRuleRepository;
import com.example.catalog.transfer.show.price.ShowPriceCalculationRequest;
import com.example.catalog.transfer.show.price.ShowPriceCalculationResponse;
import com.example.common.enumeration.OperationStatus;
import com.example.common.enumeration.SeatType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShowPricingServiceTest extends BaseTest {

    @Autowired
    private IShowPriceRuleRepository showPriceRuleRepository;

    @Autowired
    private ShowPricingService showPricingService;

    @Test
    void testCalculateShowPrice_Found() {
        ShowPriceRule rule = new ShowPriceRule(
                "SHOW123", // showSystemCode
                SeatType.REGULAR, // seatType
                BigDecimal.valueOf(100), // price
                Currency.getInstance("INR"));
        showPriceRuleRepository.save(rule);

        ShowPriceCalculationRequest request = new ShowPriceCalculationRequest();
        request.setUserSystemCode("USER456");
        request.setShowSystemCode("SHOW123");
        request.setSeatType(SeatType.REGULAR);
        request.setQuantity(3);

        ShowPriceCalculationResponse response = showPricingService.calculateShowPrice(request);

        assertEquals("SHOW123", response.getShowSystemCode());
        assertEquals(BigDecimal.valueOf(300), response.getPrice());
        assertEquals(Currency.getInstance("INR"), response.getCurrency());
    }

    @Test
    void testCalculateShowPrice_NotFound() {
        ShowPriceCalculationRequest request = new ShowPriceCalculationRequest();
        request.setUserSystemCode("USER456");
        request.setShowSystemCode("NONEXISTENT");
        request.setSeatType(SeatType.PREMIUM);
        request.setQuantity(2);

        ShowPriceCalculationResponse response = showPricingService.calculateShowPrice(request);

        assertEquals(OperationStatus.FAILURE, response.getStatus());
        assertEquals("ShowPriceRule not found", response.getMessage());
    }
}