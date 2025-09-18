package com.example.catalog.repository.jpa;

import com.example.catalog.domain.jpa.City;

import java.util.Optional;

public interface ICityRepository extends IRelationEntityRepository<City> {

    Optional<City> findBySystemCode(String systemCode);
}
