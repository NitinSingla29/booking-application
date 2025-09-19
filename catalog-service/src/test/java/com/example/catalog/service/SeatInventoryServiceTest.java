package com.example.catalog.service;

import com.example.catalog.BaseTest;
import com.example.catalog.domain.jpa.*;
import com.example.catalog.enumeration.SeatInventoryStatus;
import com.example.catalog.enumeration.SeatType;
import com.example.catalog.enumeration.ShowStatus;
import com.example.catalog.repository.jpa.*;
import com.example.catalog.transfer.client.SeatHoldRequest;
import com.example.catalog.transfer.show.ShowResponse;
import com.example.catalog.transfer.show.ShowSaveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeatInventoryServiceTest extends BaseTest {

    @Autowired
    private ShowService showService;

    @Autowired
    private SeatInventoryService seatInventoryService;

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

    private Movie movie1;
    private Theatre theatre1;
    private Screen screen1;

    @BeforeEach
    void setUp() {
        // Cities
        City city1 = new City();
        city1.setName("Test City");
        City city2 = new City();
        city2.setName("Other City");

        // Theatres
        theatre1 = new Theatre("Test Theatre", city1, "123 Main St", "Suite 1", "12345");
        theatreRepository.save(theatre1);

        // Movies
        movie1 = new Movie("Test Movie", 120, "English", "Drama");
        movieRepository.save(movie1);

        // Screens and seats for both theatres
        screen1 = createScreenWithSeats("Screen 1", theatre1);
        screenRepository.save(screen1);

    }

    @Test
    void testHoldSeats_Success() {
        ShowResponse show = createShow(movie1, screen1, theatre1, LocalDate.now(), ShowStatus.SCHEDULED);
        Show savedShow = showRepository.findBySystemCode(show.getSystemCode()).orElseThrow();
        List<SeatInventoryEntry> entries = seatInventoryEntryRepository.findByShow(savedShow);

        List<String> seatCodes = List.of(entries.get(0).getSeatLayoutDefinition().getSeatCode(), entries.get(1).getSeatLayoutDefinition().getSeatCode());

        SeatHoldRequest req = new SeatHoldRequest();
        req.setShowSystemCode(show.getSystemCode());
        req.setSeatCodes(seatCodes);
        req.setBookingSystemCode("BOOK123");

        var resp = seatInventoryService.holdSeats(req);
        assertEquals("SUCCESS", resp.getStatus().name());
        assertEquals(seatCodes.size(), resp.getSeatCodes().size());
        assertEquals("BOOK123", resp.getBookingSystemCode());
    }

    @Test
    void testHoldSeats_ShowNotFound() {
        SeatHoldRequest req = new SeatHoldRequest();
        req.setShowSystemCode("INVALID_CODE");
        req.setSeatCodes(List.of("A1", "A2"));
        req.setBookingSystemCode("BOOK123");

        var resp = seatInventoryService.holdSeats(req);
        assertEquals("FAILURE", resp.getStatus().name());
        assertEquals("Show not found", resp.getMessage());
    }

    @Test
    void testHoldSeats_SeatNotFound() {
        ShowResponse show = createShow(movie1, screen1, theatre1, LocalDate.now(), ShowStatus.SCHEDULED);

        SeatHoldRequest req = new SeatHoldRequest();
        req.setShowSystemCode(show.getSystemCode());
        req.setSeatCodes(List.of("NON_EXISTENT_SEAT"));
        req.setBookingSystemCode("BOOK123");

        var resp = seatInventoryService.holdSeats(req);
        assertEquals("FAILURE", resp.getStatus().name());
        assertEquals("Some seats not found", resp.getMessage());
    }

    @Test
    void testHoldSeats_SeatNotAvailable() {
        ShowResponse show = createShow(movie1, screen1, theatre1, LocalDate.now(), ShowStatus.SCHEDULED);
        Show savedShow = showRepository.findBySystemCode(show.getSystemCode()).orElseThrow();
        List<SeatInventoryEntry> entries = seatInventoryEntryRepository.findByShow(savedShow);

        // Mark one seat as HOLD
        SeatInventoryEntry entry = entries.get(0);
        entry.setSeatInventoryStatus(SeatInventoryStatus.HOLD);
        seatInventoryEntryRepository.save(entry);

        SeatHoldRequest req = new SeatHoldRequest();
        req.setShowSystemCode(show.getSystemCode());
        req.setSeatCodes(List.of(entry.getSeatLayoutDefinition().getSeatCode()));
        req.setBookingSystemCode("BOOK123");

        var resp = seatInventoryService.holdSeats(req);
        assertEquals("FAILURE", resp.getStatus().name());
        assertEquals("Some seats are not available", resp.getMessage());
    }

    private Screen createScreenWithSeats(String name, Theatre theatre) {
        Screen screen = new Screen();
        screen.setName(name);
        screen.setTheatre(theatre);

        List<SeatLayoutDefinition> seatDefs = new ArrayList<>();
        for (int row = 1; row <= 5; row++) {
            for (int col = 1; col <= 10; col++) {
                SeatType seatType = (row == 1) ? SeatType.PREMIUM : SeatType.REGULAR;
                SeatLayoutDefinition seat = new SeatLayoutDefinition(screen, name + "R" + row + "C" + col, seatType, row, col);
                seatDefs.add(seat);
            }
        }
        screen.setSeatLayoutDefinitions(seatDefs);
        return screen;
    }

    private ShowResponse createShow(Movie movie, Screen screen, Theatre theatre, LocalDate showDate, ShowStatus status) {
        ShowSaveRequest req = new ShowSaveRequest();
        req.setMovieSystemCode(movie.getSystemCode());
        req.setScreenSystemCode(screen.getSystemCode());
        req.setTheatreSystemCode(theatre.getSystemCode());
        req.setStartTime(LocalDateTime.now());
        req.setEndTime(LocalDateTime.now().plusHours(2));
        req.setShowDate(showDate);
        req.setShowStatus(status);

        return showService.saveShow(req);
    }
}
