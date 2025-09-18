package com.example.catalog.domain.jpa;

import com.example.catalog.domain.jpa.base.SystemCodedRelationEntity;
import com.example.catalog.enumeration.SeatType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

@Entity
@Getter
@Setter
@Table(name = "seat_layout", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"screen_id", "seat_code"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatDefinition extends SystemCodedRelationEntity {

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

    public SeatDefinition(Screen screen, String seatCode, SeatType seatType, int rowNumber, int columnNumber) {
        Assert.notNull(screen, "Argument [screen] must be present");
        this.screen = screen;
        this.seatCode = seatCode;
        this.seatType = seatType;
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
    }
}
