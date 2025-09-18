package com.example.inventory.service;


import com.example.inventory.domain.jpa.*;
import com.example.inventory.enumeration.SeatInventoryStatus;
import com.example.inventory.enumeration.ShowStatus;
import com.example.inventory.repository.jpa.*;
import com.example.inventory.transfer.show.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ShowService {

    @Autowired
    private IShowRepository showRepository;
    @Autowired
    private IMovieRepository movieRepository;
    @Autowired
    private IScreenRepository screenRepository;
    @Autowired
    private ITheatreRepository theatreRepository;
    @Autowired
    private ISeatInventoryEntryRepository seatInventoryEntryRepository;

    @Transactional
    public ShowResponse saveShow(ShowSaveRequest request) {
        Movie movie = movieRepository.findBySystemCode(request.getMovieSystemCode()).orElse(null);
        Screen screen = screenRepository.findBySystemCode(request.getScreenSystemCode()).orElse(null);
        Theatre theatre = theatreRepository.findBySystemCode(request.getTheatreSystemCode()).orElse(null);

        Show show = new Show(movie, screen, theatre, request.getStartTime(), request.getEndTime(), request.getShowDate(), request.getShowStatus());
        Show saved = showRepository.save(show);

        // If SCHEDULED, create SeatInventoryEntry for all seats in the screen
        if (request.getShowStatus() == ShowStatus.SCHEDULED && screen != null && screen.getSeatDefinitions() != null) {
            for (SeatDefinition seat : screen.getSeatDefinitions()) {
                SeatInventoryEntry entry = new SeatInventoryEntry();
                entry.setShow(saved);
                entry.setSeatLayout(seat);
                entry.setSeatInventoryStatus(SeatInventoryStatus.AVAILABLE);
                seatInventoryEntryRepository.save(entry);
            }
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ShowResponse getShowBySystemCode(String systemCode) {
        return showRepository.findBySystemCode(systemCode).map(this::toResponse).orElse(null);
    }

    @Transactional
    public ShowResponse updateShowBySystemCode(ShowUpdateRequest request) {
        Optional<Show> opt = showRepository.findBySystemCode(request.getSystemCode());
        if (opt.isEmpty()) {
            return null;
        }
        Show show = opt.get();

        if (request.getMovieSystemCode() != null) {
            Movie movie = movieRepository.findBySystemCode(request.getMovieSystemCode()).orElse(null);
            show.setMovieId(movie);
        }
        if (request.getScreenSystemCode() != null) {
            Screen screen = screenRepository.findBySystemCode(request.getScreenSystemCode()).orElse(null);
            show.setScreen(screen);
        }
        if (request.getTheatreSystemCode() != null) {
            Theatre theatre = theatreRepository.findBySystemCode(request.getTheatreSystemCode()).orElse(null);
            show.setTheatreId(theatre);
        }
        if (request.getStartTime() != null) {
            show.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            show.setEndTime(request.getEndTime());
        }
        if (request.getShowDate() != null) {
            show.setShowDate(request.getShowDate());
        }
        if (request.getShowStatus() != null) {
            show.setShowStatus(request.getShowStatus());
        }

        Show updated = showRepository.save(show);
        return toResponse(updated);
    }

    @Transactional
    public void deleteShowBySystemCode(String systemCode) {
        showRepository.findBySystemCode(systemCode).ifPresent(showRepository::delete);
    }

    @Transactional(readOnly = true)
    public ShowSeatInventoryResponse getSeatInventoryForShow(ShowSeatInventoryRequest request) {
        Optional<Show> showOpt = showRepository.findBySystemCode(request.getShowSystemCode());
        if (showOpt.isEmpty()) {
            return null;
        }
        Show show = showOpt.get();

        List<SeatInventoryEntry> entries;
        if (request.getSeatStatus() != null) {
            entries = seatInventoryEntryRepository.findByShowAndSeatInventoryStatus(show, request.getSeatStatus());
        } else {
            entries = seatInventoryEntryRepository.findByShow(show);
        }

        List<ShowSeatInventoryResponse.SeatInfo> seatInfos = entries.stream().map(ShowService::getSeatInfo).toList();

        ShowSeatInventoryResponse response = new ShowSeatInventoryResponse();
        response.setSeats(seatInfos);
        return response;
    }

    private static ShowSeatInventoryResponse.SeatInfo getSeatInfo(SeatInventoryEntry entry) {
        ShowSeatInventoryResponse.SeatInfo info = new ShowSeatInventoryResponse.SeatInfo();
        info.setSeatSystemCode(entry.getSeatLayout().getSystemCode());
        info.setSeatCode(entry.getSeatLayout().getSeatCode());
        info.setSeatStatus(entry.getSeatInventoryStatus());
        return info;
    }

    private ShowResponse toResponse(Show show) {
        ShowResponse resp = new ShowResponse();
        resp.setId(show.getId());
        resp.setSystemCode(show.getSystemCode());
        resp.setMovieSystemCode(show.getMovieId() != null ? show.getMovieId().getSystemCode() : null);
        resp.setScreenSystemCode(show.getScreen() != null ? show.getScreen().getSystemCode() : null);
        resp.setTheatreSystemCode(show.getTheatreId() != null ? show.getTheatreId().getSystemCode() : null);
        resp.setStartTime(show.getStartTime());
        resp.setEndTime(show.getEndTime());
        resp.setShowDate(show.getShowDate());
        resp.setShowStatus(show.getShowStatus());
        return resp;
    }
}