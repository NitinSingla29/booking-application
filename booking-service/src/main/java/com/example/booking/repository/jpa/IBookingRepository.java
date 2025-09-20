package com.example.booking.repository.jpa;

import com.example.booking.domain.jpa.Booking;
import com.example.booking.repository.jpa.base.IRelationEntityRepository;

import java.util.Optional;

public interface IBookingRepository extends IRelationEntityRepository<Booking> {
    Optional<Booking> findBySystemCode(String bookingSystemCode);
}
