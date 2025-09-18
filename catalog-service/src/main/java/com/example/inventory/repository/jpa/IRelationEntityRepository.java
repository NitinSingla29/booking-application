package com.example.inventory.repository.jpa;

import com.example.inventory.domain.jpa.RelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IRelationEntityRepository<T extends RelationEntity> extends JpaRepository<T, Long> {
}
