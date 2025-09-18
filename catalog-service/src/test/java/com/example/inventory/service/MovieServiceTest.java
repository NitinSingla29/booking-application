package com.example.inventory.service;

import com.example.inventory.transfer.movie.MovieSaveRequest;
import com.example.inventory.transfer.movie.MovieUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class MovieServiceTest extends BaseTest {

    @Autowired
    private MovieService movieService;

    @Test
    void testSaveMovie() {
        MovieSaveRequest request = new MovieSaveRequest();
        request.setTitle("Test Movie");
        request.setDurationMin(120);
        request.setLanguage("English");
        request.setGenres("Action");
        request.setPosterUrl("url");
        request.setRating(8.5);

        var response = movieService.saveMovie(request);
        assertNotNull(response.getId());
        assertNotNull(response.getSystemCode());
    }

    @Test
    void testGetMovieBySystemCode_Found() {
        MovieSaveRequest request = new MovieSaveRequest();
        request.setTitle("Test Movie");
        request.setDurationMin(120);
        request.setLanguage("English");
        request.setGenres("Action");
        request.setPosterUrl("url");
        request.setRating(8.5);
        var saveResponse = movieService.saveMovie(request);

        var response = movieService.getMovieBySystemCode(saveResponse.getSystemCode());
        assertNotNull(response);
        assertEquals("Test Movie", response.getTitle());
    }

    @Test
    void testGetMovieBySystemCode_NotFound() {
        var response = movieService.getMovieBySystemCode("nonexistent");
        assertNull(response);
    }

    @Test
    void testUpdateMovieBySystemCode_Found() {
        MovieSaveRequest saveRequest = new MovieSaveRequest();
        saveRequest.setTitle("Test Movie");
        saveRequest.setDurationMin(120);
        saveRequest.setLanguage("English");
        saveRequest.setGenres("Action");
        saveRequest.setPosterUrl("url");
        saveRequest.setRating(8.5);
        var saveResponse = movieService.saveMovie(saveRequest);

        MovieUpdateRequest updateRequest = new MovieUpdateRequest();
        updateRequest.setSystemCode(saveResponse.getSystemCode());
        updateRequest.setTitle("Updated Title");
        updateRequest.setDurationMin(130);
        updateRequest.setLanguage("French");
        updateRequest.setGenres("Drama");
        updateRequest.setPosterUrl("newurl");
        updateRequest.setRating(9.0);

        var response = movieService.updateMovieBySystemCode(updateRequest);
        assertNotNull(response);
        assertEquals("Updated Title", response.getTitle());
        assertEquals(130, response.getDurationMin());
        assertEquals("French", response.getLanguage());
        assertEquals("Drama", response.getGenres());
        assertEquals("newurl", response.getPosterUrl());
        assertEquals(9.0, response.getRating());
    }

    @Test
    void testUpdateMovieBySystemCode_NotFound() {
        MovieUpdateRequest updateRequest = new MovieUpdateRequest();
        updateRequest.setSystemCode("nonexistent");
        updateRequest.setTitle("Updated Title");
        updateRequest.setDurationMin(130);
        updateRequest.setLanguage("French");
        updateRequest.setGenres("Drama");
        updateRequest.setPosterUrl("newurl");
        updateRequest.setRating(9.0);

        var response = movieService.updateMovieBySystemCode(updateRequest);
        assertNull(response);
    }

    @Test
    void testDeleteMovieBySystemCode() {
        MovieSaveRequest request = new MovieSaveRequest();
        request.setTitle("Test Movie");
        request.setDurationMin(120);
        request.setLanguage("English");
        request.setGenres("Action");
        request.setPosterUrl("url");
        request.setRating(8.5);
        var saveResponse = movieService.saveMovie(request);

        movieService.deleteMovieBySystemCode(saveResponse.getSystemCode());
        var response = movieService.getMovieBySystemCode(saveResponse.getSystemCode());
        assertNull(response);
    }
}