package com.example.catalog.repository.jpa;

import com.example.catalog.domain.jpa.Movie;
import com.example.core.repository.jpa.IRelationEntityRepository;

import java.util.Optional;

public interface IMovieRepository extends IRelationEntityRepository<Movie> {

    Optional<Movie> findBySystemCode(String systemCode);
}
