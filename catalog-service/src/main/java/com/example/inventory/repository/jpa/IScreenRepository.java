package com.example.inventory.repository.jpa;

import com.example.inventory.domain.jpa.Screen;

import java.util.Optional;

public interface IScreenRepository extends IRelationEntityRepository<Screen> {

    Optional<Screen> findBySystemCode(String systemCode);
}
