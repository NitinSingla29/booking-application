package com.example.inventory.repository.jpa;

import com.example.inventory.domain.jpa.Show;

import java.util.Optional;

public interface IShowRepository extends IRelationEntityRepository<Show> {

    Optional<Show> findBySystemCode(String systemCode);
}
