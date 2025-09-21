package com.example.catalog.controller;

import com.example.catalog.BaseTest;
import com.example.catalog.domain.jpa.*;
import com.example.catalog.enumeration.SeatInventoryStatus;
import com.example.catalog.enumeration.ShowStatus;
import com.example.catalog.repository.jpa.*;
import com.example.catalog.transfer.client.SeatConfirmRequest;
import com.example.catalog.transfer.client.SeatHoldRequest;
import com.example.catalog.transfer.client.SeatReleaseRequest;
import com.example.common.enumeration.SeatType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class SeatInventoryControllerTest extends BaseTest {

    public static final String INVENTORY_URL_PREFIX = "/inventory/seat";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ISeatInventoryEntryRepository seatInventoryEntryRepository;

    @Autowired
    private IMovieRepository movieRepository;
    @Autowired
    private IScreenRepository screenRepository;
    @Autowired
    private ITheatreRepository theatreRepository;
    @Autowired
    private IShowRepository showRepository;

    private Movie movie;
    private Screen screen;
    private Theatre theatre;
    private Show show;

    @BeforeEach
    void setUp() {
        movie = new Movie("Test Movie", 120, "English", "Drama");
        movieRepository.save(movie);

        City city = new City();
        city.setName("Test City");
        theatre = new Theatre("Test Theatre", city, "123 Main St", "Suite 1", "12345");
        theatreRepository.save(theatre);

        screen = new Screen();
        screen.setName("Screen 1");
        screen.setTheatre(theatre);
        screenRepository.save(screen);

        show = new Show(movie, screen, theatre, LocalDateTime.now(), LocalDateTime.now().plusHours(2), LocalDate.now(), ShowStatus.SCHEDULED);
        showRepository.save(show);
    }


    @Test
    void testHoldSeats_Success() throws Exception {
        // Create a seat for the screen using the constructor
        SeatLayoutDefinition seat = new SeatLayoutDefinition(screen, "A1", SeatType.REGULAR, 1, 1);
        screen.getSeatLayoutDefinitions().add(seat);

        // Save screen and seat
        screenRepository.save(screen);

        // Create SeatInventoryEntry for the show
        SeatInventoryEntry entry = new SeatInventoryEntry();
        entry.setShow(show);
        entry.setSeatLayoutDefinition(seat);
        entry.setSeatInventoryStatus(SeatInventoryStatus.AVAILABLE);

        // Save entry
        seatInventoryEntryRepository.save(entry);

        // Prepare request
        SeatHoldRequest req = new SeatHoldRequest();
        req.setShowSystemCode(show.getSystemCode());
        req.setSeatCodes(List.of("A1"));
        req.setBookingSystemCode("BOOK123");

        mockMvc.perform(post(INVENTORY_URL_PREFIX + "/hold-seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.seatCodes[0]").value("A1"))
                .andExpect(jsonPath("$.bookingSystemCode").value("BOOK123"));
    }

    @Test
    void testHoldSeats_ShowNotFound() throws Exception {
        SeatHoldRequest req = new SeatHoldRequest();
        req.setShowSystemCode("INVALID_SHOW");
        req.setSeatCodes(List.of("A1"));
        req.setBookingSystemCode("BOOK123");

        mockMvc.perform(post(INVENTORY_URL_PREFIX + "/hold-seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILURE"))
                .andExpect(jsonPath("$.message").value("Show not found"));
    }

    @Test
    void testHoldSeats_SeatNotFound() throws Exception {
        // No seat "A2" exists
        SeatHoldRequest req = new SeatHoldRequest();
        req.setShowSystemCode(show.getSystemCode());
        req.setSeatCodes(List.of("A2"));
        req.setBookingSystemCode("BOOK123");

        mockMvc.perform(post(INVENTORY_URL_PREFIX + "/hold-seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILURE"))
                .andExpect(jsonPath("$.message").value("Some seats not found"));
    }

    @Test
    void testHoldSeats_SeatNotAvailable() throws Exception {
        // Create seat and entry, but mark as HOLD
        SeatLayoutDefinition seat = new SeatLayoutDefinition(screen, "A3", SeatType.REGULAR, 1, 2);
        screen.getSeatLayoutDefinitions().add(seat);
        screenRepository.save(screen);

        SeatInventoryEntry entry = new SeatInventoryEntry();
        entry.setShow(show);
        entry.setSeatLayoutDefinition(seat);
        entry.setSeatInventoryStatus(SeatInventoryStatus.HOLD);
        seatInventoryEntryRepository.save(entry);

        SeatHoldRequest req = new SeatHoldRequest();
        req.setShowSystemCode(show.getSystemCode());
        req.setSeatCodes(List.of("A3"));
        req.setBookingSystemCode("BOOK123");

        mockMvc.perform(post(INVENTORY_URL_PREFIX + "/hold-seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILURE"))
                .andExpect(jsonPath("$.message").value("Some seats are not available"));
    }

    @Test
    void testReleaseSeats_NoSeatsFound() throws Exception {
        var req = new SeatReleaseRequest("NON_EXISTENT_CODE");

        mockMvc.perform(post(INVENTORY_URL_PREFIX + "/release-seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILURE"))
                .andExpect(jsonPath("$.message").value("No seats found for booking code"));
    }

    @Test
    void testReleaseSeats_SeatsAlreadyReleased() throws Exception {
        SeatLayoutDefinition seat = new SeatLayoutDefinition(screen, "B1", SeatType.REGULAR, 2, 1);
        screen.getSeatLayoutDefinitions().add(seat);
        screenRepository.save(screen);

        SeatInventoryEntry entry = new SeatInventoryEntry();
        entry.setShow(show);
        entry.setSeatLayoutDefinition(seat);
        entry.setSeatInventoryStatus(SeatInventoryStatus.AVAILABLE);
        entry.setBookingSystemCode("BOOK456");
        seatInventoryEntryRepository.save(entry);

        var req = new SeatReleaseRequest();
        req.setBookingSystemCode("BOOK456");

        mockMvc.perform(post(INVENTORY_URL_PREFIX + "/release-seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SEATS_ALREADY_RELEASED"))
                .andExpect(jsonPath("$.message").value("Seats are already released"));
    }

    @Test
    void testReleaseSeats_SeatsAreConfirmed() throws Exception {
        SeatLayoutDefinition seat = new SeatLayoutDefinition(screen, "B2", SeatType.REGULAR, 2, 2);
        screen.getSeatLayoutDefinitions().add(seat);
        screenRepository.save(screen);

        SeatInventoryEntry entry = new SeatInventoryEntry();
        entry.setShow(show);
        entry.setSeatLayoutDefinition(seat);
        entry.setSeatInventoryStatus(SeatInventoryStatus.CONFIRMED);
        entry.setBookingSystemCode("BOOK789");
        seatInventoryEntryRepository.save(entry);

        var req = new SeatReleaseRequest();
        req.setBookingSystemCode("BOOK789");

        mockMvc.perform(post(INVENTORY_URL_PREFIX + "/release-seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SEATS_ARE_CONFIRMED"))
                .andExpect(jsonPath("$.message").value("Seats are already confirmed"));
    }

    @Test
    void testReleaseSeats_Success() throws Exception {
        SeatLayoutDefinition seat = new SeatLayoutDefinition(screen, "B3", SeatType.REGULAR, 2, 3);
        screen.getSeatLayoutDefinitions().add(seat);
        screenRepository.save(screen);

        SeatInventoryEntry entry = new SeatInventoryEntry();
        entry.setShow(show);
        entry.setSeatLayoutDefinition(seat);
        entry.setSeatInventoryStatus(SeatInventoryStatus.HOLD);
        entry.setBookingSystemCode("BOOK999");
        seatInventoryEntryRepository.save(entry);

        var req = new SeatReleaseRequest();
        req.setBookingSystemCode("BOOK999");

        mockMvc.perform(post(INVENTORY_URL_PREFIX + "/release-seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Seats released successfully"))
                .andExpect(jsonPath("$.bookingSystemCode").value("BOOK999"))
                .andExpect(jsonPath("$.seatCodes[0]").value("B3"));
    }

    @Test
    void testConfirmSeats_NoSeatsFound() throws Exception {
        var req = new SeatConfirmRequest("NON_EXISTENT_CODE");

        mockMvc.perform(post(INVENTORY_URL_PREFIX + "/confirm-seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILURE"))
                .andExpect(jsonPath("$.message").value("No seats found for booking code"));
    }

    @Test
    void testConfirmSeats_SeatsNotHold() throws Exception {
        // Create seat and entry, mark as AVAILABLE
        SeatLayoutDefinition seat = new SeatLayoutDefinition(screen, "C1", SeatType.REGULAR, 3, 1);
        screen.getSeatLayoutDefinitions().add(seat);
        screenRepository.save(screen);

        SeatInventoryEntry entry = new SeatInventoryEntry();
        entry.setShow(show);
        entry.setSeatLayoutDefinition(seat);
        entry.setSeatInventoryStatus(SeatInventoryStatus.AVAILABLE);
        entry.setBookingSystemCode("BOOK321");
        seatInventoryEntryRepository.save(entry);

        var req = new SeatConfirmRequest();
        req.setBookingSystemCode("BOOK321");

        mockMvc.perform(post(INVENTORY_URL_PREFIX + "/confirm-seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILURE"))
                .andExpect(jsonPath("$.message").value("Seats must be in hold state before confirmation"));
    }

    @Test
    void testConfirmSeats_Success() throws Exception {
        // Create seat and entry, mark as HOLD
        SeatLayoutDefinition seat = new SeatLayoutDefinition(screen, "C2", SeatType.REGULAR, 3, 2);
        screen.getSeatLayoutDefinitions().add(seat);
        screenRepository.save(screen);

        SeatInventoryEntry entry = new SeatInventoryEntry();
        entry.setShow(show);
        entry.setSeatLayoutDefinition(seat);
        entry.setSeatInventoryStatus(SeatInventoryStatus.HOLD);
        entry.setBookingSystemCode("BOOK654");
        seatInventoryEntryRepository.save(entry);

        var req = new SeatConfirmRequest();
        req.setBookingSystemCode("BOOK654");

        mockMvc.perform(post(INVENTORY_URL_PREFIX + "/confirm-seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Seats confirmed successfully"))
                .andExpect(jsonPath("$.bookingSystemCode").value("BOOK654"));
    }
}
