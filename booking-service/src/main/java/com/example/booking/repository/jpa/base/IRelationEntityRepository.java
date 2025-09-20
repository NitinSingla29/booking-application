package com.example.booking.repository.jpa.base;

import com.example.core.domain.jpa.RelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IRelationEntityRepository<T extends RelationEntity> extends JpaRepository<T, Long> {
}
