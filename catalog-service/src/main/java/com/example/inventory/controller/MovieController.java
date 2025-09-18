package com.example.inventory.controller;

import com.example.inventory.service.MovieService;
import com.example.inventory.transfer.movie.MovieResponse;
import com.example.inventory.transfer.movie.MovieSaveRequest;
import com.example.inventory.transfer.movie.MovieSaveResponse;
import com.example.inventory.transfer.movie.MovieUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @PostMapping
    public ResponseEntity<MovieSaveResponse> createMovie(@RequestBody MovieSaveRequest request) {
        MovieSaveResponse response = movieService.saveMovie(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{systemCode}")
    public ResponseEntity<MovieResponse> getMovie(@PathVariable String systemCode) {
        MovieResponse response = movieService.getMovieBySystemCode(systemCode);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{systemCode}")
    public ResponseEntity<MovieResponse> updateMovie(
            @PathVariable String systemCode,
            @RequestBody MovieUpdateRequest request) {
        request.setSystemCode(systemCode);
        MovieResponse response = movieService.updateMovieBySystemCode(request);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{systemCode}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String systemCode) {
        movieService.deleteMovieBySystemCode(systemCode);
        return ResponseEntity.noContent().build();
    }
}
