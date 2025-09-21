package com.example.catalog.service;


import com.example.catalog.BaseTest;
import com.example.catalog.domain.jpa.City;
import com.example.catalog.repository.jpa.ICityRepository;
import com.example.catalog.transfer.theatre.ScreenSaveRequest;
import com.example.catalog.transfer.theatre.SeatDefinitionSaveRequest;
import com.example.catalog.transfer.theatre.TheatreResponse;
import com.example.catalog.transfer.theatre.TheatreSaveRequest;
import com.example.common.enumeration.SeatType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TheatreServiceTest extends BaseTest {

    @Autowired
    private TheatreService theatreService;

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

    @Test
    void testSaveTheatre() {
        Long cityId = createCityAndGetId();
        TheatreSaveRequest request = buildTheatreRequest(cityId);

        TheatreResponse response = theatreService.saveTheatre(request);
        assertNotNull(response.getId());
        assertNotNull(response.getSystemCode());
        assertEquals("Test Theatre", response.getName());
        assertEquals(cityId, response.getCityId());
        assertEquals(1, response.getScreens().size());
        assertEquals("Screen 1", response.getScreens().get(0).getName());
        assertEquals(1, response.getScreens().get(0).getSeats().size());
        assertEquals("A1", response.getScreens().get(0).getSeats().get(0).getSeatCode());
    }

    @Test
    void testGetTheatreBySystemCode_Found() {
        Long cityId = createCityAndGetId();
        TheatreSaveRequest request = buildTheatreRequest(cityId);
        TheatreResponse saved = theatreService.saveTheatre(request);

        TheatreResponse response = theatreService.getTheatreBySystemCode(saved.getSystemCode());
        assertNotNull(response);
        assertEquals("Test Theatre", response.getName());
    }

    @Test
    void testGetTheatreBySystemCode_NotFound() {
        TheatreResponse response = theatreService.getTheatreBySystemCode("nonexistent");
        assertNull(response);
    }

    @Test
    void testUpdateTheatreBySystemCode_Found() {
        Long cityId = createCityAndGetId();
        TheatreSaveRequest request = buildTheatreRequest(cityId);
        TheatreResponse saved = theatreService.saveTheatre(request);

        TheatreSaveRequest updateRequest = buildTheatreRequest(cityId);
        updateRequest.setName("Updated Theatre");
        updateRequest.setAddressLine("456 New St");
        updateRequest.setZipCode("54321");

        TheatreResponse updated = theatreService.updateTheatreBySystemCode(saved.getSystemCode(), updateRequest);
        assertNotNull(updated);
        assertEquals("Updated Theatre", updated.getName());
        assertEquals("456 New St", updated.getAddressLine());
        assertEquals("54321", updated.getZipCode());
    }

    @Test
    void testUpdateTheatreBySystemCode_NotFound() {
        Long cityId = createCityAndGetId();
        TheatreSaveRequest updateRequest = buildTheatreRequest(cityId);
        TheatreResponse updated = theatreService.updateTheatreBySystemCode("nonexistent", updateRequest);
        assertNull(updated);
    }

    @Test
    void testDeleteTheatreBySystemCode() {
        Long cityId = createCityAndGetId();
        TheatreSaveRequest request = buildTheatreRequest(cityId);
        TheatreResponse saved = theatreService.saveTheatre(request);

        theatreService.deleteTheatreBySystemCode(saved.getSystemCode());
        TheatreResponse response = theatreService.getTheatreBySystemCode(saved.getSystemCode());
        assertNull(response);
    }
}