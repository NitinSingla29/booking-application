package com.example.catalog.repository.jpa;

import com.example.catalog.domain.jpa.Screen;

import java.util.Optional;

public interface IScreenRepository extends IRelationEntityRepository<Screen> {

    Optional<Screen> findBySystemCode(String systemCode);
}
