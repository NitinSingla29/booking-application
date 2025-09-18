package com.example.inventory.domain.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "theatre")
public class Theatre extends SystemCodedRelationEntity {

    @Column(name = "name")
    private String name;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @Column(name = "address_line")
    private String addressLine;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "zip_code")
    private String zipCode;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "theatre", orphanRemoval = true)
    private List<Screen> screens = new ArrayList<>();

    public Theatre(String name, City city, String addressLine, String addressLine2, String zipCode) {
        this.name = name;
        this.city = city;
        this.addressLine = addressLine;
        this.addressLine2 = addressLine2;
        this.zipCode = zipCode;
    }
}
