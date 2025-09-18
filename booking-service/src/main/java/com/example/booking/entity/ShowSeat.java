package com.example.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "show_seat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowSeat {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(columnDefinition = "BINARY(16)")
    private UUID showId;

    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Version
    private Long version;

    public enum Status { AVAILABLE, HELD, BOOKED }
}
