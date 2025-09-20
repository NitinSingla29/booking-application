package com.example.catalog.domain.jpa;

import com.example.booking.enumeration.SeatType;
import com.example.catalog.domain.jpa.base.SystemCodedRelationEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

@Entity
@Getter
@Setter
@Table(name = "seat_layout_definition")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatLayoutDefinition extends SystemCodedRelationEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(name = "seat_code")
    private String seatCode;

    @Column(name = "seat_type")
    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    @Column(name = "row_num")
    private int rowNumber;

    @Column(name = "column_num")
    private int columnNumber;

    public SeatLayoutDefinition(Screen screen, String seatCode, SeatType seatType, int rowNumber, int columnNumber) {
        Assert.notNull(screen, "Argument [screen] must be present");
        this.screen = screen;
        this.seatCode = seatCode;
        this.seatType = seatType;
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
    }
}
