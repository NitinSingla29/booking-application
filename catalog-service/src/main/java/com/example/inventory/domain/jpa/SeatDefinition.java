package com.example.inventory.domain.jpa;

import com.example.inventory.enumeration.SeatType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "seat_layout", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"screen_id", "seat_code"})})
public class SeatDefinition extends RelationEntity {

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    private Screen screen;

    @Column(name = "seat_code")
    private String seatCode;

    @Column(name = "seat_type")
    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    @Column(name = "row_number")
    private int rowNumber;

    @Column(name = "column_number")
    private int columnNumber;

}
