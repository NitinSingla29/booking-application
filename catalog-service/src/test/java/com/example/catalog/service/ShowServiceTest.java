package com.example.catalog.service;

import com.example.catalog.BaseTest;
import com.example.catalog.domain.jpa.*;
import com.example.catalog.enumeration.SeatInventoryStatus;
import com.example.catalog.enumeration.ShowStatus;
import com.example.catalog.repository.jpa.*;
import com.example.catalog.transfer.show.*;
import com.example.core.enumeration.SeatType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    private Movie movie1, movie2;
    private Theatre theatre1, theatre2;
    private Screen screen1, screen2;
    private SeatLayoutDefinition seatDef1, seatDef2;

    @BeforeEach
    void setUp() {
        // Cities
        City city1 = new City();
        city1.setName("Test City");
        City city2 = new City();
        city2.setName("Other City");

        // Theatres
        theatre1 = new Theatre("Test Theatre", city1, "123 Main St", "Suite 1", "12345");
        theatre2 = new Theatre("Other Theatre", city2, "456 Side St", "Suite 2", "54321");
        theatreRepository.save(theatre1);
        theatreRepository.save(theatre2);

        // Movies
        movie1 = new Movie("Test Movie", 120, "English", "Drama");
        movie2 = new Movie("Other Movie", 90, "Hindi", "Comedy");
        movieRepository.save(movie1);
        movieRepository.save(movie2);

        // Screens and seats for both theatres
        screen1 = createScreenWithSeats("Screen 1", theatre1);
        screen2 = createScreenWithSeats("Screen 2", theatre2);

        screenRepository.save(screen1);
        screenRepository.save(screen2);

        seatDef1 = screen1.getSeatLayoutDefinitions().get(0);
        seatDef2 = screen2.getSeatLayoutDefinitions().get(0);
    }


    @Test
    void testSaveShowAndSeatInventory() {
        ShowResponse resp = createShow(movie1, screen1, theatre1, LocalDate.now(), ShowStatus.SCHEDULED);

        assertNotNull(resp);
        assertEquals(movie1.getSystemCode(), resp.getMovieSystemCode());
        assertEquals(screen1.getSystemCode(), resp.getScreenSystemCode());
        assertEquals(theatre1.getSystemCode(), resp.getTheatreSystemCode());

        Show show = showRepository.findBySystemCode(resp.getSystemCode()).orElse(null);
        assertNotNull(show);

        List<SeatInventoryEntry> entries = seatInventoryEntryRepository.findByShow(show);
        assertEquals(50, entries.size());
        assertEquals(SeatInventoryStatus.AVAILABLE, entries.get(0).getSeatInventoryStatus());
    }

    @Test
    void testGetShowBySystemCode() {
        ShowResponse show = createShow(movie1, screen1, theatre1, LocalDate.now(), ShowStatus.SCHEDULED);
        String systemCode = show.getSystemCode();

        ShowResponse resp = showService.getShowBySystemCode(systemCode);
        assertNotNull(resp);
        assertEquals(systemCode, resp.getSystemCode());
    }

    @Test
    void testUpdateShowBySystemCode() {
        ShowResponse show = createShow(movie1, screen1, theatre1, LocalDate.now(), ShowStatus.SCHEDULED);

        String systemCode = show.getSystemCode();

        ShowUpdateRequest req = new ShowUpdateRequest();
        req.setSystemCode(systemCode);
        req.setShowStatus(ShowStatus.CANCELLED);

        ShowResponse resp = showService.updateShowBySystemCode(req);
        assertNotNull(resp);
        assertEquals(ShowStatus.CANCELLED, resp.getShowStatus());
    }

    @Test
    void testGetSeatInventoryForShow_AllSeats() {
        ShowResponse show = createShow(movie1, screen1, theatre1, LocalDate.now(), ShowStatus.SCHEDULED);

        String systemCode = show.getSystemCode();

        ShowSeatInventoryRequest req = new ShowSeatInventoryRequest();
        req.setShowSystemCode(systemCode);

        ShowSeatInventoryResponse resp = showService.getSeatInventoryForShow(req);
        assertNotNull(resp);
        assertEquals(50, resp.getSeats().size());
    }

    @Test
    void testGetSeatInventoryForShow_FilteredByStatus() {
        ShowResponse show = createShow(movie1, screen1, theatre1, LocalDate.now(), ShowStatus.SCHEDULED);
        String systemCode = show.getSystemCode();

        ShowSeatInventoryRequest req = new ShowSeatInventoryRequest();
        req.setShowSystemCode(systemCode);
        req.setSeatStatus(SeatInventoryStatus.AVAILABLE);

        ShowSeatInventoryResponse resp = showService.getSeatInventoryForShow(req);
        assertNotNull(resp);
        assertEquals(50, resp.getSeats().size());
        assertEquals(SeatInventoryStatus.AVAILABLE, resp.getSeats().get(0).getSeatStatus());
    }

    @Test
    void testFindShows_FilterByCityMovieDate() {
        // Create shows in both cities, movies, and dates
        createShow(movie1, screen1, theatre1, LocalDate.of(2024, 6, 1), ShowStatus.SCHEDULED);
        createShow(movie2, screen2, theatre2, LocalDate.of(2024, 6, 2), ShowStatus.SCHEDULED);

        ShowListingRequest filterReq = new ShowListingRequest();
        filterReq.setCity("Test City");
        filterReq.setMovieTitle("Test Movie");
        filterReq.setMovieDate(LocalDate.of(2024, 6, 1));
        filterReq.setPageSize(10);
        filterReq.setPageNumber(0);

        var page = showService.findShows(filterReq);
        assertEquals(1, page.getTotalElements());
        assertEquals("Test Movie", movie1.getTitle());
    }

    @Test
    void testFindShows_Pagination() {
        // Create 15 shows for pagination in Test City
        for (int i = 0; i < 15; i++) {
            createShow(movie1, screen1, theatre1, LocalDate.now(), ShowStatus.SCHEDULED);
        }

        ShowListingRequest pageReq = new ShowListingRequest();
        pageReq.setCity("Test City");
        pageReq.setMovieTitle("Test Movie");
        pageReq.setMovieDate(LocalDate.now());
        pageReq.setPageSize(10);
        pageReq.setPageNumber(0);

        var page1 = showService.findShows(pageReq);
        assertEquals(10, page1.getContent().size());
        assertEquals(15, page1.getTotalElements());

        pageReq.setPageNumber(1);
        var page2 = showService.findShows(pageReq);
        assertEquals(5, page2.getContent().size());
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
