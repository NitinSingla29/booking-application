package com.example.catalog.service;

import com.example.catalog.component.LockHandle;
import com.example.catalog.component.LockManager;
import com.example.catalog.domain.jpa.SeatInventoryEntry;
import com.example.catalog.domain.jpa.Show;
import com.example.catalog.enumeration.SeatInventoryStatus;
import com.example.catalog.enumeration.SeatReleaseStatus;
import com.example.catalog.enumeration.SeatReservationStatus;
import com.example.catalog.repository.jpa.ISeatInventoryEntryRepository;
import com.example.catalog.repository.jpa.IShowRepository;
import com.example.catalog.transfer.client.SeatHoldRequest;
import com.example.catalog.transfer.client.SeatHoldResponse;
import com.example.catalog.transfer.client.SeatReleaseRequest;
import com.example.catalog.transfer.client.SeatReleaseResponse;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeatInventoryService {

    @Autowired
    private ISeatInventoryEntryRepository seatInventoryEntryRepository;

    @Autowired
    private IShowRepository showRepository;

    @Value("${seat.hold.period.mins:5}")
    private int defaultSeatHoldPeriodMins;

    @Autowired
    private LockManager lockManager;

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
        List<String> lockKeys = getLockKeys(entries, request.getShowSystemCode());
        LockHandle lockHandle = lockManager.acquireLocks(lockKeys);
        try {
            for (SeatInventoryEntry entry : entries) {
                holdSeat(request.getBookingSystemCode(), entry, show, holdExpiresAt);
            }

            seatInventoryEntryRepository.saveAllAndFlush(entries);

            return getSeatHoldResponse(request, entries, show);
        } catch (OptimisticLockException e) {
            return new SeatHoldResponse(SeatReservationStatus.FAILURE, "Seats are already acquired by another transaction");
        } finally {
            lockManager.releaseLock(lockHandle);
        }
    }

    private static void holdSeat(String bookingSystemCode, SeatInventoryEntry entry, Show show, LocalDateTime holdExpiresAt) {
        entry.setSeatInventoryStatus(SeatInventoryStatus.HOLD);
        entry.setBookingSystemCode(bookingSystemCode);
        entry.setShow(show);
        entry.setHoldExpiresAt(holdExpiresAt);
    }

    private static SeatHoldResponse getSeatHoldResponse(SeatHoldRequest request, List<SeatInventoryEntry> entries, Show show) {
        SeatHoldResponse seatsReservedSuccessfully = new SeatHoldResponse(SeatReservationStatus.SUCCESS, "Seats reserved successfully");
        seatsReservedSuccessfully.setSeatCodes(entries.stream().map(e -> e.getSeatLayoutDefinition().getSeatCode()).toList());
        seatsReservedSuccessfully.setBookingSystemCode(request.getBookingSystemCode());
        seatsReservedSuccessfully.setShowSystemCode(request.getShowSystemCode());
        seatsReservedSuccessfully.setScreenSystemCode(show.getScreen().getSystemCode());
        seatsReservedSuccessfully.setScreenSystemCode(show.getTheatre().getSystemCode());
        seatsReservedSuccessfully.setMovieSystemCode(show.getMovie().getSystemCode());
        return seatsReservedSuccessfully;
    }

    @Transactional
    public SeatReleaseResponse releaseSeats(SeatReleaseRequest seatReleaseRequest) {
        List<SeatInventoryEntry> entries = seatInventoryEntryRepository
                .findByBookingSystemCode(seatReleaseRequest.getBookingSystemCode());

        if (entries.isEmpty()) {
            return new SeatReleaseResponse(SeatReleaseStatus.FAILURE, "No seats found for booking code");
        }

        boolean allAvailable = entries.stream()
                .allMatch(e -> e.getSeatInventoryStatus() == SeatInventoryStatus.AVAILABLE);
        boolean anyConfirmed = entries.stream()
                .anyMatch(e -> e.getSeatInventoryStatus() == SeatInventoryStatus.CONFIRMED);

        if (allAvailable) {
            return new SeatReleaseResponse(SeatReleaseStatus.SEATS_ALREADY_RELEASED, "Seats are already released");
        }
        if (anyConfirmed) {
            return new SeatReleaseResponse(SeatReleaseStatus.SEATS_ARE_CONFIRMED, "Seats are already confirmed");
        }

        List<SeatInventoryEntry> heldEntries = entries.stream()
                .filter(e -> e.getSeatInventoryStatus() == SeatInventoryStatus.HOLD)
                .toList();

        if (heldEntries.isEmpty()) {
            return new SeatReleaseResponse(SeatReleaseStatus.FAILURE, "No held seats found for booking code");
        }

        for (SeatInventoryEntry entry : heldEntries) {
            entry.setSeatInventoryStatus(SeatInventoryStatus.AVAILABLE);
            entry.setBookingSystemCode(null);
            entry.setHoldExpiresAt(null);
        }
        seatInventoryEntryRepository.saveAllAndFlush(heldEntries);

        SeatReleaseResponse resp = new SeatReleaseResponse();
        resp.setStatus(SeatReleaseStatus.SUCCESS);
        resp.setMessage("Seats released successfully");
        resp.setBookingSystemCode(seatReleaseRequest.getBookingSystemCode());
        resp.setSeatCodes(heldEntries.stream()
                .map(e -> e.getSeatLayoutDefinition().getSeatCode())
                .toList());
        return resp;
    }

    private static List<String> getLockKeys(List<SeatInventoryEntry> seatInventoryEntries, String showSystemCode) {
        return seatInventoryEntries.stream().map(e -> e.getSeatLayoutDefinition().getSeatCode())
                .map(s -> "show:" + showSystemCode + ":seat:" + s)
                .collect(Collectors.toList());
    }


}
