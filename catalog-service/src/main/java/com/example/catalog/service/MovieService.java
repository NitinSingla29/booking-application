// catalog-service/src/main/java/com/example/inventory/service/MovieService.java
package com.example.catalog.service;

import com.example.catalog.domain.jpa.Movie;
import com.example.catalog.repository.jpa.IMovieRepository;
import com.example.catalog.transfer.movie.MovieResponse;
import com.example.catalog.transfer.movie.MovieSaveRequest;
import com.example.catalog.transfer.movie.MovieSaveResponse;
import com.example.catalog.transfer.movie.MovieUpdateRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    private IMovieRepository movieRepository;

    @Transactional
    public MovieSaveResponse saveMovie(MovieSaveRequest request) {
        Movie movie = new Movie();
        movie.setTitle(request.getTitle());
        movie.setDurationMin(request.getDurationMin());
        movie.setLanguage(request.getLanguage());
        movie.setGenres(request.getGenres());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setRating(request.getRating());
        Movie saved = movieRepository.save(movie);

        MovieSaveResponse response = new MovieSaveResponse();
        response.setId(saved.getId());
        response.setSystemCode(saved.getSystemCode() != null ? saved.getSystemCode().toString() : null);
        return response;
    }

    public MovieResponse getMovieBySystemCode(String systemCode) {
        Optional<Movie> movieOpt = movieRepository.findBySystemCode(systemCode);
        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();
            MovieResponse response = new MovieResponse();
            response.setId(movie.getId());
            response.setSystemCode(movie.getSystemCode());
            response.setTitle(movie.getTitle());
            response.setDurationMin(movie.getDurationMin());
            response.setLanguage(movie.getLanguage());
            response.setGenres(movie.getGenres());
            response.setPosterUrl(movie.getPosterUrl());
            response.setRating(movie.getRating());
            return response;
        }
        return null;
    }


    @Transactional
    public MovieResponse updateMovieBySystemCode(MovieUpdateRequest request) {
        Optional<Movie> movieOpt = movieRepository.findBySystemCode(request.getSystemCode());
        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();
            movie.setTitle(request.getTitle());
            movie.setDurationMin(request.getDurationMin());
            movie.setLanguage(request.getLanguage());
            movie.setGenres(request.getGenres());
            movie.setPosterUrl(request.getPosterUrl());
            movie.setRating(request.getRating());
            Movie updated = movieRepository.save(movie);

            MovieResponse response = getMovieDetail(updated);
            return response;
        }
        return null;
    }

    private MovieResponse getMovieDetail(Movie updated) {
        MovieResponse response = new MovieResponse();
        response.setId(updated.getId());
        response.setSystemCode(updated.getSystemCode() != null ? updated.getSystemCode().toString() : null);
        response.setTitle(updated.getTitle());
        response.setDurationMin(updated.getDurationMin());
        response.setLanguage(updated.getLanguage());
        response.setGenres(updated.getGenres());
        response.setPosterUrl(updated.getPosterUrl());
        response.setRating(updated.getRating());
        return response;
    }

    @Transactional
    public void deleteMovieBySystemCode(String systemCode) {
        Optional<Movie> movieOpt = movieRepository.findBySystemCode(systemCode);
        movieOpt.ifPresent(movieRepository::delete);
    }
}
