package com.example.catalog.service;

import com.example.catalog.domain.jpa.City;
import com.example.catalog.domain.jpa.Screen;
import com.example.catalog.domain.jpa.SeatDefinition;
import com.example.catalog.domain.jpa.Theatre;
import com.example.catalog.repository.jpa.ICityRepository;
import com.example.catalog.repository.jpa.ITheatreRepository;
import com.example.catalog.transfer.theatre.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TheatreService {

    @Autowired
    private ITheatreRepository theatreRepository;

    @Autowired
    private ICityRepository cityRepository;

    @Transactional
    public TheatreResponse saveTheatre(TheatreSaveRequest request) {
        City city = cityRepository.findById(request.getCityId()).orElse(null);
        Theatre theatre = new Theatre(request.getName(), city, request.getAddressLine(), request.getAddressLine2(), request.getZipCode());

        if (request.getScreens() != null) {
            for (ScreenSaveRequest screenReq : request.getScreens()) {
                addScreenInTheatre(screenReq, theatre);
            }
        }

        Theatre saved = theatreRepository.save(theatre);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TheatreResponse getTheatreBySystemCode(String systemCode) {
        return theatreRepository.findBySystemCode(systemCode)
                .map(this::toResponse)
                .orElse(null);
    }

    @Transactional
    public TheatreResponse updateTheatreBySystemCode(String systemCode, TheatreSaveRequest request) {
        Optional<Theatre> opt = theatreRepository.findBySystemCode(systemCode);
        if (opt.isEmpty()) {
            return null;
        }
        Theatre theatre = opt.get();

        theatre.setName(request.getName());
        theatre.setAddressLine(request.getAddressLine());
        theatre.setAddressLine2(request.getAddressLine2());
        theatre.setZipCode(request.getZipCode());
        if (request.getCityId() != null) {
            City city = cityRepository.findById(request.getCityId()).orElse(null);
            theatre.setCity(city);
        }

        // For simplicity, replace screens and seats
        theatre.getScreens().clear();
        if (request.getScreens() != null) {
            for (ScreenSaveRequest screenReq : request.getScreens()) {
                addScreenInTheatre(screenReq, theatre);
            }
        }

        Theatre updated = theatreRepository.save(theatre);
        return toResponse(updated);
    }


    @Transactional
    public void deleteTheatreBySystemCode(String systemCode) {
        theatreRepository.findBySystemCode(systemCode)
                .ifPresent(theatreRepository::delete);
    }

    private void addScreenInTheatre(ScreenSaveRequest screenReq, Theatre theatre) {
        Screen screen = new Screen();
        screen.setName(screenReq.getName());
        screen.setTheatre(theatre);

        if (screenReq.getSeats() != null) {
            for (SeatDefinitionSaveRequest seatReq : screenReq.getSeats()) {
                SeatDefinition seat = new SeatDefinition(
                        screen,
                        seatReq.getSeatCode(),
                        seatReq.getSeatType(),
                        seatReq.getRowNumber(),
                        seatReq.getColumnNumber()
                );
                screen.getSeatDefinitions().add(seat);
            }
        }
        theatre.getScreens().add(screen);
    }

    private TheatreResponse toResponse(Theatre theatre) {
        TheatreResponse resp = new TheatreResponse();
        resp.setId(theatre.getId());
        resp.setSystemCode(theatre.getSystemCode());
        resp.setName(theatre.getName());
        resp.setAddressLine(theatre.getAddressLine());
        resp.setAddressLine2(theatre.getAddressLine2());
        resp.setZipCode(theatre.getZipCode());
        resp.setCityId(theatre.getCity() != null ? theatre.getCity().getId() : null);

        if (theatre.getScreens() != null) {
            resp.setScreens(theatre.getScreens().stream().map(this::toScreenResponse).collect(Collectors.toList()));
        }
        return resp;
    }

    private ScreenResponse toScreenResponse(Screen screen) {
        ScreenResponse resp = new ScreenResponse();
        resp.setId(screen.getId());
        resp.setSystemCode(screen.getSystemCode());
        resp.setName(screen.getName());
        if (screen.getSeatDefinitions() != null) {
            resp.setSeats(screen.getSeatDefinitions().stream().map(this::toSeatResponse).collect(Collectors.toList()));
        }
        return resp;
    }

    private SeatDefinitionResponse toSeatResponse(SeatDefinition seat) {
        SeatDefinitionResponse resp = new SeatDefinitionResponse();
        resp.setId(seat.getId());
        resp.setSystemCode(seat.getSystemCode());
        resp.setSeatCode(seat.getSeatCode());
        resp.setSeatType(seat.getSeatType());
        resp.setRowNumber(seat.getRowNumber());
        resp.setColumnNumber(seat.getColumnNumber());
        return resp;
    }
}