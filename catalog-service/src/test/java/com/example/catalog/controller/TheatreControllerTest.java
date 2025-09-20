package com.example.catalog.controller;


import com.example.booking.enumeration.SeatType;
import com.example.catalog.BaseTest;
import com.example.catalog.domain.jpa.City;
import com.example.catalog.repository.jpa.ICityRepository;
import com.example.catalog.transfer.theatre.ScreenSaveRequest;
import com.example.catalog.transfer.theatre.SeatDefinitionSaveRequest;
import com.example.catalog.transfer.theatre.TheatreSaveRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class TheatreControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ICityRepository cityRepository;

    private Long createCityAndGetId() {
        City city = new City();
        city.setName("Test City");
        city = cityRepository.save(city);
        return city.getId();
    }

    private TheatreSaveRequest buildTheatreRequest(Long cityId) {
        SeatDefinitionSaveRequest seat1 = new SeatDefinitionSaveRequest();
        seat1.setSeatCode("A1");
        seat1.setSeatType(SeatType.REGULAR);
        seat1.setRowNumber(1);
        seat1.setColumnNumber(1);

        ScreenSaveRequest screen1 = new ScreenSaveRequest();
        screen1.setName("Screen 1");
        screen1.setSeats(List.of(seat1));

        TheatreSaveRequest request = new TheatreSaveRequest();
        request.setName("Test Theatre");
        request.setCityId(cityId);
        request.setAddressLine("123 Main St");
        request.setAddressLine2("Suite 100");
        request.setZipCode("12345");
        request.setScreens(List.of(screen1));
        return request;
    }

    private String createTheatreAndGetSystemCode() throws Exception {
        Long cityId = createCityAndGetId();
        TheatreSaveRequest request = buildTheatreRequest(cityId);

        var result = mockMvc.perform(post("/catalog/theatres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.systemCode").exists())
                .andReturn();

        var json = result.getResponse().getContentAsString();
        return objectMapper.readTree(json).get("systemCode").asText();
    }

    @Test
    void testCreateTheatre() throws Exception {
        Long cityId = createCityAndGetId();
        TheatreSaveRequest request = buildTheatreRequest(cityId);

        mockMvc.perform(post("/catalog/theatres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.systemCode").exists());
    }

    @Test
    void testGetTheatre_Found() throws Exception {
        String systemCode = createTheatreAndGetSystemCode();

        mockMvc.perform(get("/catalog/theatres/" + systemCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Theatre"));
    }

    @Test
    void testGetTheatre_NotFound() throws Exception {
        mockMvc.perform(get("/catalog/theatres/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateTheatre_Found() throws Exception {
        String systemCode = createTheatreAndGetSystemCode();

        Long cityId = createCityAndGetId();
        TheatreSaveRequest updateRequest = buildTheatreRequest(cityId);
        updateRequest.setName("Updated Theatre");
        updateRequest.setAddressLine("456 New St");
        updateRequest.setZipCode("54321");

        mockMvc.perform(put("/catalog/theatres/" + systemCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Theatre"))
                .andExpect(jsonPath("$.addressLine").value("456 New St"))
                .andExpect(jsonPath("$.zipCode").value("54321"));
    }

    @Test
    void testUpdateTheatre_NotFound() throws Exception {
        Long cityId = createCityAndGetId();
        TheatreSaveRequest updateRequest = buildTheatreRequest(cityId);

        mockMvc.perform(put("/catalog/theatres/nonexistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTheatre() throws Exception {
        String systemCode = createTheatreAndGetSystemCode();

        mockMvc.perform(delete("/catalog/theatres/" + systemCode))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/catalog/theatres/" + systemCode))
                .andExpect(status().isNotFound());
    }
}