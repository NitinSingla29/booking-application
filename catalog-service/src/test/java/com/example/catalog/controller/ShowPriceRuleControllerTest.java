package com.example.catalog.controller;


import com.example.catalog.BaseTest;
import com.example.catalog.enumeration.OperationStatus;
import com.example.catalog.enumeration.SeatType;
import com.example.catalog.transfer.show.price.ShowPriceRuleCreateRequest;
import com.example.catalog.transfer.show.price.ShowPriceRuleCreateResponse;
import com.example.catalog.transfer.show.price.ShowPriceRuleUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ShowPriceRuleControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateShowPriceRule() throws Exception {
        ShowPriceRuleCreateRequest request = new ShowPriceRuleCreateRequest();
        request.setShowSystemCode("SHOW123");
        request.setSeatType(SeatType.REGULAR);
        request.setPrice(BigDecimal.valueOf(100));
        request.setCurrency(Currency.getInstance("USD"));

        String responseJson = mockMvc.perform(post("/catalogue/price-rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ShowPriceRuleCreateResponse response = objectMapper.readValue(responseJson, ShowPriceRuleCreateResponse.class);

        assertNotNull(response.getRuleSystemCode());
        assertEquals(OperationStatus.SUCCESS, response.getStatus());
    }


    @Test
    void testGetShowPriceRuleById_NotFound() throws Exception {
        mockMvc.perform(get("/catalogue/price-rule/{ruleSystemCode}", "NON_EXISTENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILURE"));
    }

    @Test
    void testUpdateShowPriceRule() throws Exception {
        // First, create a rule
        ShowPriceRuleCreateRequest createRequest = new ShowPriceRuleCreateRequest();
        createRequest.setShowSystemCode("SHOW456");
        createRequest.setSeatType(SeatType.PREMIUM);
        createRequest.setPrice(BigDecimal.valueOf(200));
        createRequest.setCurrency(Currency.getInstance("USD"));

        String createResponse = mockMvc.perform(post("/catalogue/price-rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        ShowPriceRuleUpdateRequest updateRequest = new ShowPriceRuleUpdateRequest();
        updateRequest.setShowSystemCode("SHOW456");
        updateRequest.setSeatType(SeatType.PREMIUM);
        updateRequest.setPrice(BigDecimal.valueOf(250));
        updateRequest.setCurrency(Currency.getInstance("USD"));

        mockMvc.perform(put("/catalogue/price-rule/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(250));
    }

    @Test
    void testDeleteShowPriceRule() throws Exception {
        // Create a rule first
        ShowPriceRuleCreateRequest createRequest = new ShowPriceRuleCreateRequest();
        createRequest.setShowSystemCode("SHOW_DEL");
        createRequest.setSeatType(SeatType.REGULAR);
        createRequest.setPrice(BigDecimal.valueOf(99));
        createRequest.setCurrency(Currency.getInstance("USD"));

        String createResponseJson = mockMvc.perform(post("/catalogue/price-rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ShowPriceRuleCreateResponse createResponse = objectMapper.readValue(createResponseJson, ShowPriceRuleCreateResponse.class);
        String ruleSystemCode = createResponse.getRuleSystemCode();

        // Delete the rule
        mockMvc.perform(delete("/catalogue/price-rule/{ruleSystemCode}", ruleSystemCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testDeleteNonExistentShowPriceRule() throws Exception {
        mockMvc.perform(delete("/catalogue/price-rule/{ruleSystemCode}", "NON_EXISTENT_CODE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILURE"));
    }
}