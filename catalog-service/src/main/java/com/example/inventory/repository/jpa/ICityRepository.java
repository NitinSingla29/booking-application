package com.example.inventory.repository.jpa;

import com.example.inventory.domain.jpa.City;

import java.util.Optional;

public interface ICityRepository extends IRelationEntityRepository<City> {

    Optional<City> findBySystemCode(String systemCode);
}
