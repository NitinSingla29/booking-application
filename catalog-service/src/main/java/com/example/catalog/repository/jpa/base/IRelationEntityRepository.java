package com.example.catalog.repository.jpa.base;

import com.example.catalog.domain.jpa.base.RelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IRelationEntityRepository<T extends RelationEntity> extends JpaRepository<T, Long> {
}
