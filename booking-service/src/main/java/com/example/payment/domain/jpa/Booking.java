package com.example.payment.domain.jpa;

import com.example.core.domain.jpa.SystemCodedRelationEntity;
import com.example.payment.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Entity
@Table(name = "booking")
@Getter
public class Booking extends SystemCodedRelationEntity {

    @Column(name = "user_system_code", nullable = false)
    private String userSystemCode;

    @Column(name = "show_system_code", nullable = false)
    private String showSystemCode;

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
        this.seatCodes = new ArrayList<>(seatCodes);
        this.status = status;
    }
}
