package com.example.inventory.repository.jpa;

import com.example.inventory.domain.jpa.Theatre;

import java.util.Optional;

public interface ITheatreRepository extends IRelationEntityRepository<Theatre> {

    Optional<Theatre> findBySystemCode(String systemCode);
}
