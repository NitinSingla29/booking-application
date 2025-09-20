package com.example.catalog.domain.jpa;

import com.example.catalog.enumeration.SeatInventoryStatus;
import com.example.core.domain.jpa.SystemCodedRelationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "seat_inventory_entry")
@Getter
@Setter
public class SeatInventoryEntry extends SystemCodedRelationEntity {

    @Column(name = "booking_system_code", nullable = true)
    private String bookingSystemCode;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id")
    private Show show;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_layout_definition_id")
    private SeatLayoutDefinition seatLayoutDefinition;

    @Column(name = "seat_inventory_status")
    @Enumerated(EnumType.STRING)
    private SeatInventoryStatus seatInventoryStatus;

    @Column(name = "booked_by", nullable = true)
    private String bookedBy;

    @Column(name = "hold_expires_at", nullable = true)
    private LocalDateTime holdExpiresAt;


}
