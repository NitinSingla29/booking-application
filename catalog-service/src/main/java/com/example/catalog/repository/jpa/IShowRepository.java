package com.example.catalog.repository.jpa;

import com.example.catalog.domain.jpa.Show;
import com.example.catalog.repository.jpa.base.IRelationEntityRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface IShowRepository extends IRelationEntityRepository<Show>, JpaSpecificationExecutor<Show> {

    Optional<Show> findBySystemCode(String systemCode);
}
