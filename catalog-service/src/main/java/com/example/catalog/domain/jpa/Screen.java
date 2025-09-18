package com.example.catalog.domain.jpa;

import com.example.catalog.domain.jpa.base.SystemCodedRelationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screen")
@Getter
@Setter
public class Screen extends SystemCodedRelationEntity {

    @ManyToOne
    @JoinColumn(name = "theatre_id")
    private Theatre theatre;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SeatDefinition> seatDefinitions = new ArrayList<>();

}
