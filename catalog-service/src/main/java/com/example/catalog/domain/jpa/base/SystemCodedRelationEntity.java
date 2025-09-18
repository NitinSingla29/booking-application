package com.example.catalog.domain.jpa.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;

import java.util.UUID;

@MappedSuperclass
@Getter
public abstract class SystemCodedRelationEntity extends RelationEntity {

    @Column(name = "system_code")
    private String systemCode;


    @PrePersist
    protected void prePersist() {
        if (systemCode == null) {
            systemCode = UUID.randomUUID().toString();
        }
    }
}
