package com.example.catalog.domain.jpa;

import com.example.core.domain.jpa.SystemCodedRelationEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "city")
public class City extends SystemCodedRelationEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "time_zone")
    private String timezone;

    @Column(name = "country")
    private String country;

    public City(String name, String timezone, String country) {
        this.name = name;
        this.timezone = timezone;
        this.country = country;
    }
}
