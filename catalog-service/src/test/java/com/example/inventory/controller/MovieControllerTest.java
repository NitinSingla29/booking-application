package com.example.inventory.controller;


import com.example.inventory.BaseTest;
import com.example.inventory.transfer.movie.MovieSaveRequest;
import com.example.inventory.transfer.movie.MovieUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class MovieControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String createMovieAndGetSystemCode() throws Exception {
        MovieSaveRequest request = new MovieSaveRequest();
        request.setTitle("Test Movie");
        request.setDurationMin(120);
        request.setLanguage("English");
        request.setGenres("Action");
        request.setPosterUrl("url");
        request.setRating(8.5);

        var result = mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.systemCode").exists())
                .andReturn();

        var json = result.getResponse().getContentAsString();
        return objectMapper.readTree(json).get("systemCode").asText();
    }

    @Test
    void testCreateMovie() throws Exception {
        MovieSaveRequest request = new MovieSaveRequest();
        request.setTitle("Test Movie");
        request.setDurationMin(120);
        request.setLanguage("English");
        request.setGenres("Action");
        request.setPosterUrl("url");
        request.setRating(8.5);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.systemCode").exists());
    }

    @Test
    void testGetMovie_Found() throws Exception {
        String systemCode = createMovieAndGetSystemCode();

        mockMvc.perform(get("/movies/" + systemCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Movie"));
    }

    @Test
    void testGetMovie_NotFound() throws Exception {
        mockMvc.perform(get("/movies/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateMovie_Found() throws Exception {
        String systemCode = createMovieAndGetSystemCode();

        MovieUpdateRequest updateRequest = new MovieUpdateRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDurationMin(130);
        updateRequest.setLanguage("French");
        updateRequest.setGenres("Drama");
        updateRequest.setPosterUrl("newurl");
        updateRequest.setRating(9.0);

        mockMvc.perform(put("/movies/" + systemCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void testUpdateMovie_NotFound() throws Exception {
        MovieUpdateRequest updateRequest = new MovieUpdateRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDurationMin(130);
        updateRequest.setLanguage("French");
        updateRequest.setGenres("Drama");
        updateRequest.setPosterUrl("newurl");
        updateRequest.setRating(9.0);

        mockMvc.perform(put("/movies/nonexistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteMovie() throws Exception {
        String systemCode = createMovieAndGetSystemCode();

        mockMvc.perform(delete("/movies/" + systemCode))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/movies/" + systemCode))
                .andExpect(status().isNotFound());
    }
}