package com.example.catalog.controller;


import com.example.catalog.BaseTest;
import com.example.catalog.transfer.show.price.ShowPriceRuleCreateRequest;
import com.example.catalog.transfer.show.price.ShowPriceRuleCreateResponse;
import com.example.catalog.transfer.show.price.ShowPriceRuleUpdateRequest;
import com.example.catalog.transfer.show.price.ShowPriceRuleUpdateResponse;
import com.example.core.enumeration.OperationStatus;
import com.example.core.enumeration.SeatType;
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
        // Create a rule first
        ShowPriceRuleCreateRequest createRequest = new ShowPriceRuleCreateRequest();
        createRequest.setShowSystemCode("SHOW_UPD");
        createRequest.setSeatType(SeatType.REGULAR);
        createRequest.setPrice(BigDecimal.valueOf(50));
        createRequest.setCurrency(Currency.getInstance("USD"));

        String createResponseJson = mockMvc.perform(post("/catalogue/price-rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ShowPriceRuleCreateResponse createResponse = objectMapper.readValue(createResponseJson, ShowPriceRuleCreateResponse.class);

        // Prepare update request
        ShowPriceRuleUpdateRequest updateRequest = new ShowPriceRuleUpdateRequest();
        updateRequest.setRuleSystemCode(createResponse.getRuleSystemCode());
        updateRequest.setShowSystemCode("SHOW_UPD");
        updateRequest.setSeatType(SeatType.PREMIUM);
        updateRequest.setPrice(BigDecimal.valueOf(75));
        updateRequest.setCurrency(Currency.getInstance("USD"));

        String updateResponseJson = mockMvc.perform(put("/catalogue/price-rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Assert update response
        ShowPriceRuleUpdateResponse updateResponse = objectMapper.readValue(updateResponseJson, ShowPriceRuleUpdateResponse.class);
        assertEquals(OperationStatus.SUCCESS, updateResponse.getStatus());
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