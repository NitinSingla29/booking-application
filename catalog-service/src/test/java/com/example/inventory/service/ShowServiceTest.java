package com.example.inventory.service;

import com.example.inventory.BaseTest;
import com.example.inventory.domain.jpa.*;
import com.example.inventory.enumeration.SeatInventoryStatus;
import com.example.inventory.enumeration.SeatType;
import com.example.inventory.enumeration.ShowStatus;
import com.example.inventory.repository.jpa.*;
import com.example.inventory.transfer.show.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShowServiceTest extends BaseTest {

    @Autowired
    private ShowService showService;
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

    private Movie movie;
    private Screen screen;
    private Theatre theatre;
    private SeatDefinition seatDef;

    @BeforeEach
    void setUp() {
        movie = new Movie("Test Movie", 120, "English", "Drama");
        movieRepository.save(movie);

        City city = new City();
        city.setName("Test City");
        // Set other City fields if needed

        theatre = new Theatre("Test Theatre", city, "123 Main St", "Suite 1", "12345");
        theatreRepository.save(theatre);

        screen = new Screen();
        screen.setName("Test Screen");

        List<SeatDefinition> seatDefs = new java.util.ArrayList<>();
        for (int row = 1; row <= 5; row++) {
            for (int col = 1; col <= 10; col++) {
                SeatType seatType = (row == 1) ? SeatType.PREMIUM : SeatType.STANDARD;
                SeatDefinition seat = new SeatDefinition(screen, "R" + row + "C" + col, seatType, row, col);
                seatDefs.add(seat);
            }
        }
        screen.setSeatDefinitions(seatDefs);
        screenRepository.save(screen);

        seatDef = seatDefs.get(0);
    }


    @Test
    void testSaveShowAndSeatInventory() {
        ShowSaveRequest req = new ShowSaveRequest();
        req.setMovieSystemCode(movie.getSystemCode());
        req.setScreenSystemCode(screen.getSystemCode());
        req.setTheatreSystemCode(theatre.getSystemCode());
        req.setStartTime(LocalDateTime.now());
        req.setEndTime(LocalDateTime.now().plusHours(2));
        req.setShowDate(LocalDate.now());
        req.setShowStatus(ShowStatus.SCHEDULED);

        ShowResponse resp = showService.saveShow(req);

        assertNotNull(resp);
        assertEquals(movie.getSystemCode(), resp.getMovieSystemCode());
        assertEquals(screen.getSystemCode(), resp.getScreenSystemCode());
        assertEquals(theatre.getSystemCode(), resp.getTheatreSystemCode());

        Show show = showRepository.findBySystemCode(resp.getSystemCode()).orElse(null);
        assertNotNull(show);

        List<SeatInventoryEntry> entries = seatInventoryEntryRepository.findByShow(show);
        assertEquals(1, entries.size());
        assertEquals(SeatInventoryStatus.AVAILABLE, entries.get(0).getSeatInventoryStatus());
    }

    @Test
    void testGetShowBySystemCode() {
        Show show = new Show();
        show.setMovieId(movie);
        show.setScreen(screen);
        show.setTheatreId(theatre);
        show.setStartTime(LocalDateTime.now());
        show.setEndTime(LocalDateTime.now().plusHours(2));
        show.setShowDate(LocalDate.now());
        show.setShowStatus(ShowStatus.SCHEDULED);
        showRepository.save(show);

        String systemCode = show.getSystemCode();

        ShowResponse resp = showService.getShowBySystemCode(systemCode);
        assertNotNull(resp);
        assertEquals(systemCode, resp.getSystemCode());
    }

    @Test
    void testUpdateShowBySystemCode() {
        Show show = new Show();
        show.setMovieId(movie);
        show.setScreen(screen);
        show.setTheatreId(theatre);
        show.setStartTime(LocalDateTime.now());
        show.setEndTime(LocalDateTime.now().plusHours(2));
        show.setShowDate(LocalDate.now());
        show.setShowStatus(ShowStatus.SCHEDULED);
        showRepository.save(show);

        String systemCode = show.getSystemCode();

        ShowUpdateRequest req = new ShowUpdateRequest();
        req.setSystemCode(systemCode);
        req.setShowStatus(ShowStatus.CANCELLED);

        ShowResponse resp = showService.updateShowBySystemCode(req);
        assertNotNull(resp);
        assertEquals(ShowStatus.CANCELLED, resp.getShowStatus());
    }

    @Test
    void testDeleteShowBySystemCode() {
        Show show = new Show();
        show.setMovieId(movie);
        show.setScreen(screen);
        show.setTheatreId(theatre);
        show.setStartTime(LocalDateTime.now());
        show.setEndTime(LocalDateTime.now().plusHours(2));
        show.setShowDate(LocalDate.now());
        show.setShowStatus(ShowStatus.SCHEDULED);
        showRepository.save(show);

        String systemCode = show.getSystemCode();

        showService.deleteShowBySystemCode(systemCode);
        assertFalse(showRepository.findBySystemCode(systemCode).isPresent());
    }

    @Test
    void testGetSeatInventoryForShow_AllSeats() {
        Show show = new Show();
        show.setMovieId(movie);
        show.setScreen(screen);
        show.setTheatreId(theatre);
        show.setStartTime(LocalDateTime.now());
        show.setEndTime(LocalDateTime.now().plusHours(2));
        show.setShowDate(LocalDate.now());
        show.setShowStatus(ShowStatus.SCHEDULED);
        showRepository.save(show);

        String systemCode = show.getSystemCode();

        SeatInventoryEntry entry = new SeatInventoryEntry();
        entry.setShow(show);
        entry.setSeatLayout(seatDef);
        entry.setSeatInventoryStatus(SeatInventoryStatus.AVAILABLE);
        seatInventoryEntryRepository.save(entry);

        ShowSeatInventoryRequest req = new ShowSeatInventoryRequest();
        req.setShowSystemCode(systemCode);

        ShowSeatInventoryResponse resp = showService.getSeatInventoryForShow(req);
        assertNotNull(resp);
        assertEquals(1, resp.getSeats().size());
        assertEquals(seatDef.getSystemCode(), resp.getSeats().get(0).getSeatSystemCode());
    }

    @Test
    void testGetSeatInventoryForShow_FilteredByStatus() {
        Show show = new Show();
        show.setMovieId(movie);
        show.setScreen(screen);
        show.setTheatreId(theatre);
        show.setStartTime(LocalDateTime.now());
        show.setEndTime(LocalDateTime.now().plusHours(2));
        show.setShowDate(LocalDate.now());
        show.setShowStatus(ShowStatus.SCHEDULED);
        showRepository.save(show);

        String systemCode = show.getSystemCode();

        SeatInventoryEntry entry = new SeatInventoryEntry();
        entry.setShow(show);
        entry.setSeatLayout(seatDef);
        entry.setSeatInventoryStatus(SeatInventoryStatus.AVAILABLE);
        seatInventoryEntryRepository.save(entry);

        ShowSeatInventoryRequest req = new ShowSeatInventoryRequest();
        req.setShowSystemCode(systemCode);
        req.setSeatStatus(SeatInventoryStatus.AVAILABLE);

        ShowSeatInventoryResponse resp = showService.getSeatInventoryForShow(req);
        assertNotNull(resp);
        assertEquals(1, resp.getSeats().size());
        assertEquals(SeatInventoryStatus.AVAILABLE, resp.getSeats().get(0).getSeatStatus());
    }
}