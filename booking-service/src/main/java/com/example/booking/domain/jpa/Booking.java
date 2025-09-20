package com.example.booking.domain.jpa;

import com.example.booking.BookingStatus;
import com.example.booking.domain.jpa.base.SystemCodedRelationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Entity
@Table(name = "booking")
@Getter
public class Booking extends SystemCodedRelationEntity {

    @Column(name = "user_system_code", nullable = false)
    private String userSystemCode;

    @Column(name = "theatre_system_code", nullable = false)
    private String theatreSystemCode;

    @Column(name = "movie_system_code", nullable = false)
    String movieSystemCode;

    @Column(name = "show_system_code", nullable = false)
    private String showSystemCode;

    @Column(name = "show_date", nullable = false)
    private LocalDate showDate;

    @ElementCollection
    private List<String> seatCodes = new ArrayList<>();

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private BookingStatus status;

    @Column(name = "payment_record_system_code")
    @Setter
    private String paymentRecordSystemCode;

    @Column(name = "amount")
    @Setter
    private BigDecimal amount;

    @Column(name = "currency", length = 3)
    @Setter
    private Currency currency;


    public Booking(String userSystemCode, String showSystemCode, List<String> seatCodes, BookingStatus status) {
        this.userSystemCode = userSystemCode;
        this.showSystemCode = showSystemCode;
        this.seatCodes = seatCodes;
        this.status = status;
    }
}
