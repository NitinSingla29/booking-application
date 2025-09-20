package com.example.payment.repository.jpa;

import com.example.payment.domain.jpa.Booking;
import com.example.payment.repository.jpa.base.IRelationEntityRepository;

import java.util.Optional;

public interface IBookingRepository extends IRelationEntityRepository<Booking> {
    Optional<Booking> findBySystemCode(String bookingSystemCode);
}
