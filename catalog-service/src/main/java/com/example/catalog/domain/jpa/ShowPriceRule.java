package com.example.catalog.domain.jpa;

import com.example.booking.enumeration.SeatType;
import com.example.catalog.domain.jpa.base.SystemCodedRelationEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Currency;

@Entity
@Table(name = "show_price_rule")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShowPriceRule extends SystemCodedRelationEntity {

    @Column(name = "show_system_Code", nullable = false)
    private String showSystemCode;

    @Column(name = "seat_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "currency", nullable = false)
    private Currency currency;
}
