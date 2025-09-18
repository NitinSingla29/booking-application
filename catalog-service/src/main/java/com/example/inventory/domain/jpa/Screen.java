package com.example.inventory.domain.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screen")
@Getter
@Setter
public class Screen extends RelationEntity {

    @ManyToOne
    @JoinColumn(name = "theatre_id")
    private Theatre theatre;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SeatDefinition> seatLayouts = new ArrayList<>();

}
