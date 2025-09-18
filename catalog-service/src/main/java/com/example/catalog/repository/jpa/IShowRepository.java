package com.example.catalog.repository.jpa;

import com.example.catalog.domain.jpa.Show;

import java.util.Optional;

public interface IShowRepository extends IRelationEntityRepository<Show> {

    Optional<Show> findBySystemCode(String systemCode);
}
