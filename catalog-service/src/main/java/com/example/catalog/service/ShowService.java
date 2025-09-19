package com.example.catalog.service;


import com.example.catalog.domain.jpa.*;
import com.example.catalog.enumeration.SeatInventoryStatus;
import com.example.catalog.enumeration.SeatReservationStatus;
import com.example.catalog.enumeration.ShowStatus;
import com.example.catalog.repository.jpa.*;
import com.example.catalog.transfer.client.SeatHoldRequest;
import com.example.catalog.transfer.client.SeatHoldResponse;
import com.example.catalog.transfer.show.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Value("${seat.hold.period.mins:5}")
    private int defaultSeatHoldPeriodMins;

    @Transactional
    public ShowResponse saveShow(ShowSaveRequest request) {
        Movie movie = movieRepository.findBySystemCode(request.getMovieSystemCode()).orElse(null);
        Screen screen = screenRepository.findBySystemCode(request.getScreenSystemCode()).orElse(null);
        Theatre theatre = theatreRepository.findBySystemCode(request.getTheatreSystemCode()).orElse(null);

        Show show = new Show(movie, screen, theatre, request.getStartTime(), request.getEndTime(), request.getShowDate(), request.getShowStatus());
        Show saved = showRepository.save(show);

        // If SCHEDULED, create SeatInventoryEntry for all seats in the screen
        if (request.getShowStatus() == ShowStatus.SCHEDULED && screen != null && screen.getSeatLayoutDefinitions() != null) {
            for (SeatLayoutDefinition seat : screen.getSeatLayoutDefinitions()) {
                SeatInventoryEntry entry = new SeatInventoryEntry();
                entry.setShow(saved);
                entry.setSeatLayoutDefinition(seat);
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
            show.setMovie(movie);
        }
        if (request.getScreenSystemCode() != null) {
            Screen screen = screenRepository.findBySystemCode(request.getScreenSystemCode()).orElse(null);
            show.setScreen(screen);
        }
        if (request.getTheatreSystemCode() != null) {
            Theatre theatre = theatreRepository.findBySystemCode(request.getTheatreSystemCode()).orElse(null);
            show.setTheatre(theatre);
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

    @Transactional(readOnly = true)
    public Page<ShowResponse> findShows(ShowListingRequest request) {
        Specification<Show> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (request.getCity() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("theatre").get("city").get("name"), request.getCity()));
            }
            if (request.getMovieTitle() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("movie").get("title"), request.getMovieTitle()));
            }
            if (request.getMovieDate() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("showDate"), request.getMovieDate()));
            }
            return predicate;
        };

        PageRequest pageRequest = PageRequest.of(request.getPageNumber(), request.getPageSize());
        Page<Show> shows = showRepository.findAll(spec, pageRequest);
        return shows.map(this::toResponse);
    }

    @Transactional
    public SeatHoldResponse holdSeats(SeatHoldRequest request) {
        Optional<Show> showOpt = showRepository.findBySystemCode(request.getShowSystemCode());
        if (showOpt.isEmpty()) {
            return new SeatHoldResponse(SeatReservationStatus.FAILURE, "Show not found");
        }
        Show show = showOpt.get();

        List<SeatInventoryEntry> entries = seatInventoryEntryRepository.findByShowAndSeatLayoutDefinition_SeatCodeIn(show, request.getSeatCodes());
        if (entries.size() != request.getSeatCodes().size()) {
            return new SeatHoldResponse(SeatReservationStatus.FAILURE, "Some seats not found");
        }

        for (SeatInventoryEntry entry : entries) {
            if (entry.getSeatInventoryStatus() != SeatInventoryStatus.AVAILABLE) {
                return new SeatHoldResponse(SeatReservationStatus.FAILURE, "Some seats are not available");
            }
        }

        LocalDateTime holdExpiresAt = LocalDateTime.now().plusMinutes(defaultSeatHoldPeriodMins);
        for (SeatInventoryEntry entry : entries) {
            entry.setSeatInventoryStatus(SeatInventoryStatus.HOLD);
            entry.setBookingSystemCode(request.getBookingSystemCode());
            entry.setShow(show);
            entry.setHoldExpiresAt(holdExpiresAt);
        }
        seatInventoryEntryRepository.saveAllAndFlush(entries);


        SeatHoldResponse seatsReservedSuccessfully = new SeatHoldResponse(SeatReservationStatus.SUCCESS, "Seats reserved successfully");
        seatsReservedSuccessfully.setSeatCodes(entries.stream().map(e -> e.getSeatLayoutDefinition().getSeatCode()).toList());
        seatsReservedSuccessfully.setBookingSystemCode(request.getBookingSystemCode());
        seatsReservedSuccessfully.setShowSystemCode(request.getShowSystemCode());
        seatsReservedSuccessfully.setScreenSystemCode(show.getScreen().getSystemCode());
        seatsReservedSuccessfully.setScreenSystemCode(show.getTheatre().getSystemCode());
        seatsReservedSuccessfully.setMovieSystemCode(show.getMovie().getSystemCode());
        return seatsReservedSuccessfully;
    }

    private static ShowSeatInventoryResponse.SeatInfo getSeatInfo(SeatInventoryEntry entry) {
        ShowSeatInventoryResponse.SeatInfo info = new ShowSeatInventoryResponse.SeatInfo();
        info.setSeatSystemCode(entry.getSeatLayoutDefinition().getSystemCode());
        info.setSeatCode(entry.getSeatLayoutDefinition().getSeatCode());
        info.setSeatStatus(entry.getSeatInventoryStatus());
        return info;
    }

    private ShowResponse toResponse(Show show) {
        ShowResponse resp = new ShowResponse();
        resp.setId(show.getId());
        resp.setSystemCode(show.getSystemCode());
        resp.setMovieSystemCode(show.getMovie() != null ? show.getMovie().getSystemCode() : null);
        resp.setScreenSystemCode(show.getScreen() != null ? show.getScreen().getSystemCode() : null);
        resp.setTheatreSystemCode(show.getTheatre() != null ? show.getTheatre().getSystemCode() : null);
        resp.setStartTime(show.getStartTime());
        resp.setEndTime(show.getEndTime());
        resp.setShowDate(show.getShowDate());
        resp.setShowStatus(show.getShowStatus());
        return resp;
    }
}