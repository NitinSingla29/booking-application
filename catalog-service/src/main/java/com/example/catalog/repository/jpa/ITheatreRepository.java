package com.example.catalog.repository.jpa;

import com.example.catalog.domain.jpa.Theatre;
import com.example.core.repository.jpa.IRelationEntityRepository;

import java.util.Optional;

public interface ITheatreRepository extends IRelationEntityRepository<Theatre> {

    Optional<Theatre> findBySystemCode(String systemCode);
}
