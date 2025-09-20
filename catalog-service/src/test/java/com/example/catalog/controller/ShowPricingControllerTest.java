package com.example.catalog.controller;


import com.example.booking.enumeration.SeatType;
import com.example.catalog.BaseTest;
import com.example.catalog.domain.jpa.ShowPriceRule;
import com.example.catalog.repository.jpa.IShowPriceRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Currency;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ShowPricingControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IShowPriceRuleRepository showPriceRuleRepository;

    @BeforeEach
    void setUp() {
        showPriceRuleRepository.deleteAll();
        ShowPriceRule rule = new ShowPriceRule(
                "SHOW123",
                SeatType.REGULAR,
                BigDecimal.valueOf(100),
                Currency.getInstance("USD")
        );
        showPriceRuleRepository.save(rule);
    }

    @Test
    void testCalculateShowPrice_Found() throws Exception {
        mockMvc.perform(get("/catalogue/pricing/show-price")
                        .param("showSystemCode", "SHOW123")
                        .param("seatType", "REGULAR")
                        .param("quantity", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.showSystemCode").value("SHOW123"))
                .andExpect(jsonPath("$.price").value(300))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    void testCalculateShowPrice_NotFound() throws Exception {
        mockMvc.perform(get("/catalogue/pricing/show-price")
                        .param("showSystemCode", "NONEXISTENT")
                        .param("seatType", "PREMIUM")
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILURE"))
                .andExpect(jsonPath("$.message").value("ShowPriceRule not found"));
    }
}