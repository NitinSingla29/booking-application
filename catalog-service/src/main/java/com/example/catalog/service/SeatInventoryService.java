package com.example.catalog.service;

import com.example.catalog.domain.jpa.SeatInventoryEntry;
import com.example.catalog.domain.jpa.Show;
import com.example.catalog.enumeration.SeatInventoryStatus;
import com.example.catalog.enumeration.SeatReservationStatus;
import com.example.catalog.repository.jpa.ISeatInventoryEntryRepository;
import com.example.catalog.repository.jpa.IShowRepository;
import com.example.catalog.transfer.client.SeatHoldRequest;
import com.example.catalog.transfer.client.SeatHoldResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SeatInventoryService {

    @Autowired
    private ISeatInventoryEntryRepository seatInventoryEntryRepository;

    @Autowired
    private IShowRepository showRepository;

    @Value("${seat.hold.period.mins:5}")
    private int defaultSeatHoldPeriodMins;

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
}
