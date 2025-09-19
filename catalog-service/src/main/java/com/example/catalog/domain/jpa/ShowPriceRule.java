package com.example.catalog.domain.jpa;

import com.example.catalog.domain.jpa.base.RelationEntity;
import com.example.catalog.enumeration.SeatType;
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
public class ShowPriceRule extends RelationEntity {

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
