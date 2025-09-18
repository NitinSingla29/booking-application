package com.example.catalog.domain.jpa;

import com.example.catalog.domain.jpa.base.SystemCodedRelationEntity;
import com.example.catalog.enumeration.SeatInventoryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "seat_inventory_entry")
@Getter
@Setter
public class SeatInventoryEntry extends SystemCodedRelationEntity {

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id")
    private Show show;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_layout_id")
    private SeatDefinition seatLayout;

    @Column(name = "seat_inventory_status")
    @Enumerated(EnumType.STRING)
    private SeatInventoryStatus seatInventoryStatus;

    @Column(name = "hold_expires_at", nullable = true)
    private LocalDateTime holdExpiresAt;

    @Column(name = "booking_system_code", nullable = true)
    private String bookingSystemCode;

}
