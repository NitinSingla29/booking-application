package com.example.catalog.service;


import com.example.catalog.BaseTest;
import com.example.catalog.enumeration.SeatType;
import com.example.catalog.transfer.show.price.ShowPriceRuleCreateRequest;
import com.example.catalog.transfer.show.price.ShowPriceRuleDeleteRequest;
import com.example.catalog.transfer.show.price.ShowPriceRuleUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ShowPriceRuleServiceTest extends BaseTest {

    @Autowired
    private ShowPriceRuleService showPriceRuleService;

    private static final String TEST_SHOW_SYSTEM_CODE = "SHOW123";

    @Test
    void testCreateShowPriceRule() {
        ShowPriceRuleCreateRequest request = new ShowPriceRuleCreateRequest();
        request.setShowSystemCode(TEST_SHOW_SYSTEM_CODE);
        request.setSeatType(SeatType.REGULAR);
        request.setPrice(BigDecimal.valueOf(100));
        request.setCurrency(Currency.getInstance("USD"));

        var response = showPriceRuleService.createShowPriceRule(request);
        assertNotNull(response.getId());
        assertEquals("ShowPriceRule created", response.getMessage());
    }

    @Test
    void testGetShowPriceRuleById_Found() {
        ShowPriceRuleCreateRequest createRequest = new ShowPriceRuleCreateRequest();
        createRequest.setShowSystemCode(TEST_SHOW_SYSTEM_CODE);
        createRequest.setSeatType(SeatType.REGULAR);
        createRequest.setPrice(BigDecimal.valueOf(100));
        createRequest.setCurrency(Currency.getInstance("USD"));
        var createResponse = showPriceRuleService.createShowPriceRule(createRequest);

        var response = showPriceRuleService.getShowPriceRuleById(createResponse.getId());
        assertEquals(TEST_SHOW_SYSTEM_CODE, response.getShowSystemCode());
        assertEquals(SeatType.REGULAR, response.getSeatType());
        assertEquals(BigDecimal.valueOf(100), response.getPrice());
        assertEquals(Currency.getInstance("USD"), response.getCurrency());
    }

    @Test
    void testGetShowPriceRuleById_NotFound() {
        var response = showPriceRuleService.getShowPriceRuleById(-1L);
        assertEquals("ShowPriceRule not found", response.getMessage());
    }

    @Test
    void testUpdateShowPriceRule_Found() {
        ShowPriceRuleCreateRequest createRequest = new ShowPriceRuleCreateRequest();
        createRequest.setShowSystemCode(TEST_SHOW_SYSTEM_CODE);
        createRequest.setSeatType(SeatType.REGULAR);
        createRequest.setPrice(BigDecimal.valueOf(100));
        createRequest.setCurrency(Currency.getInstance("USD"));
        showPriceRuleService.createShowPriceRule(createRequest);

        ShowPriceRuleUpdateRequest updateRequest = new ShowPriceRuleUpdateRequest();
        updateRequest.setShowSystemCode(TEST_SHOW_SYSTEM_CODE);
        updateRequest.setSeatType(SeatType.REGULAR);
        updateRequest.setPrice(BigDecimal.valueOf(150));
        updateRequest.setCurrency(Currency.getInstance("EUR"));

        var response = showPriceRuleService.updateShowPriceRule(updateRequest);
        assertEquals("ShowPriceRule updated", response.getMessage());
    }

    @Test
    void testUpdateShowPriceRule_NotFound() {
        ShowPriceRuleUpdateRequest updateRequest = new ShowPriceRuleUpdateRequest();
        updateRequest.setShowSystemCode("NONEXISTENT");
        updateRequest.setSeatType(SeatType.PREMIUM);
        updateRequest.setPrice(BigDecimal.valueOf(200));
        updateRequest.setCurrency(Currency.getInstance("USD"));

        var response = showPriceRuleService.updateShowPriceRule(updateRequest);
        assertEquals("ShowPriceRule not found", response.getMessage());
    }

    @Test
    void testDeleteShowPriceRule_Found() {
        ShowPriceRuleCreateRequest createRequest = new ShowPriceRuleCreateRequest();
        createRequest.setShowSystemCode(TEST_SHOW_SYSTEM_CODE);
        createRequest.setSeatType(SeatType.REGULAR);
        createRequest.setPrice(BigDecimal.valueOf(100));
        createRequest.setCurrency(Currency.getInstance("USD"));
        showPriceRuleService.createShowPriceRule(createRequest);

        ShowPriceRuleDeleteRequest deleteRequest = new ShowPriceRuleDeleteRequest();
        deleteRequest.setShowSystemCode(TEST_SHOW_SYSTEM_CODE);
        deleteRequest.setSeatType(SeatType.REGULAR);

        var response = showPriceRuleService.deleteShowPriceRule(deleteRequest);
        assertEquals("ShowPriceRule deleted", response.getMessage());
    }

    @Test
    void testDeleteShowPriceRule_NotFound() {
        ShowPriceRuleDeleteRequest deleteRequest = new ShowPriceRuleDeleteRequest();
        deleteRequest.setShowSystemCode("NONEXISTENT");
        deleteRequest.setSeatType(SeatType.PREMIUM);

        var response = showPriceRuleService.deleteShowPriceRule(deleteRequest);
        assertEquals("ShowPriceRule not found", response.getMessage());
    }
}