package com.example.payment.repository.jpa.base;

import com.example.payment.domain.jpa.base.RelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IRelationEntityRepository<T extends RelationEntity> extends JpaRepository<T, Long> {
}
