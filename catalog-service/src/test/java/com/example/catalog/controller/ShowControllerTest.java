package com.example.catalog.controller;

import com.example.catalog.BaseTest;
import com.example.catalog.domain.jpa.*;
import com.example.catalog.enumeration.ShowStatus;
import com.example.catalog.repository.jpa.IMovieRepository;
import com.example.catalog.repository.jpa.IScreenRepository;
import com.example.catalog.repository.jpa.IShowRepository;
import com.example.catalog.repository.jpa.ITheatreRepository;
import com.example.catalog.transfer.show.ShowListingRequest;
import com.example.catalog.transfer.show.ShowSaveRequest;
import com.example.catalog.transfer.show.ShowUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ShowControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void testCreateShow() throws Exception {
        ShowSaveRequest request = new ShowSaveRequest();
        request.setMovieSystemCode(movie.getSystemCode());
        request.setScreenSystemCode(screen.getSystemCode());
        request.setTheatreSystemCode(theatre.getSystemCode());
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusHours(2));
        request.setShowDate(LocalDate.now());
        request.setShowStatus(ShowStatus.SCHEDULED);

        mockMvc.perform(post("/api/shows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieSystemCode").value(movie.getSystemCode()));
    }

    @Test
    void testGetShow_Found() throws Exception {
        mockMvc.perform(get("/api/shows/" + show.getSystemCode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.systemCode").value(show.getSystemCode()));
    }

    @Test
    void testGetShow_NotFound() throws Exception {
        mockMvc.perform(get("/api/shows/notfound"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateShow_Found() throws Exception {
        ShowUpdateRequest request = new ShowUpdateRequest();
        request.setShowStatus(ShowStatus.CANCELLED);

        mockMvc.perform(put("/api/shows/" + show.getSystemCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.showStatus").value("CANCELLED"));
    }

    @Test
    void testUpdateShow_NotFound() throws Exception {
        ShowUpdateRequest request = new ShowUpdateRequest();
        request.setShowStatus(ShowStatus.CANCELLED);

        mockMvc.perform(put("/api/shows/notfound")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindShows_FilterByCityMovieDate() throws Exception {
        // Create another city, theatre, movie, and show
        City city2 = new City();
        city2.setName("Other City");
        Theatre theatre2 = new Theatre("Other Theatre", city2, "Addr", "Suite", "54321");
        theatreRepository.save(theatre2);

        Movie movie2 = new Movie("Other Movie", 90, "Hindi", "Comedy");
        movieRepository.save(movie2);

        Screen screen2 = new Screen();
        screen2.setName("Other Screen");
        screen2.setTheatre(theatre2);
        screenRepository.save(screen2);

        Show show2 = new Show(movie2, screen2, theatre2, LocalDateTime.now(), LocalDateTime.now().plusHours(2), LocalDate.of(2024, 6, 2), ShowStatus.SCHEDULED);
        showRepository.save(show2);

        // Filter for first city, movie, and date
        ShowListingRequest filterReq = new ShowListingRequest();
        filterReq.setCity("Test City");
        filterReq.setMovieTitle("Test Movie");
        filterReq.setMovieDate(LocalDate.now());
        filterReq.setPageSize(10);
        filterReq.setPageNumber(0);

        mockMvc.perform(post("/api/shows/listing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].movieSystemCode").value(movie.getSystemCode()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testFindShows_Pagination() throws Exception {
        // Create 15 shows for pagination
        for (int i = 0; i < 15; i++) {
            Show showPaginated = new Show(movie, screen, theatre, LocalDateTime.now().plusHours(i), LocalDateTime.now().plusHours(i + 2), LocalDate.now(), ShowStatus.SCHEDULED);
            showRepository.save(showPaginated);
        }

        ShowListingRequest pageReq = new ShowListingRequest();
        pageReq.setCity("Test City");
        pageReq.setMovieTitle("Test Movie");
        pageReq.setMovieDate(LocalDate.now());
        pageReq.setPageSize(10);
        pageReq.setPageNumber(0);

        mockMvc.perform(post("/api/shows/listing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(15));

        pageReq.setPageNumber(1);
        mockMvc.perform(post("/api/shows/listing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5));
    }

}
