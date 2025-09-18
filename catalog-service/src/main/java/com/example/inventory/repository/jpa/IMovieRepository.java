package com.example.inventory.repository.jpa;

import com.example.inventory.domain.jpa.Movie;

import java.util.Optional;

public interface IMovieRepository extends IRelationEntityRepository<Movie> {

    Optional<Movie> findBySystemCode(String systemCode);
}
