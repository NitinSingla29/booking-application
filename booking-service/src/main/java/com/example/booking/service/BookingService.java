package com.example.booking.service;

import com.example.booking.dto.BookingRequest;
import com.example.booking.dto.BookingResponse;
import com.example.booking.entity.ShowSeat;
import com.example.booking.repository.ShowSeatRepository;
import jakarta.persistence.OptimisticLockException;
import org.redisson.api.RLock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final ShowSeatRepository seatRepository;
    private final RedisLockService lockService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public BookingService(ShowSeatRepository seatRepository, RedisLockService lockService, KafkaTemplate<String, String> kafkaTemplate) {
        this.seatRepository = seatRepository;
        this.lockService = lockService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public BookingResponse bookSeats(BookingRequest request) {
        List<String> keys = request.getSeatNumbers().stream()
                .map(s -> "show:" + request.getShowId() + ":seat:" + s)
                .collect(Collectors.toList());

        RLock multiLock = null;
        try {
            multiLock = lockService.acquireMultiLock(keys, 2, 10);
            if (multiLock == null) {
                return new BookingResponse(false, "High contention on seats. Try again.", null);
            }

            // Load seats
            List<ShowSeat> seats = seatRepository.findByShowIdAndSeatNumberIn(request.getShowId(), request.getSeatNumbers());

            // Validate
            for (ShowSeat s : seats) {
                if (s.getStatus() != ShowSeat.Status.AVAILABLE) {
                    return new BookingResponse(false, "Seat " + s.getSeatNumber() + " not available", null);
                }
            }

            // Mark booked
            for (ShowSeat s : seats) {
                s.setStatus(ShowSeat.Status.BOOKED);
            }

            try {
                seatRepository.saveAll(seats);
            } catch (OptimisticLockException ole) {
                return new BookingResponse(false, "Some seats were booked in parallel. Retry.", null);
            }

            // publish event
            kafkaTemplate.send("bookings", "booking-created:" + UUID.randomUUID());

            return new BookingResponse(true, "Booking confirmed", request.getSeatNumbers());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new BookingResponse(false, "Interrupted while acquiring locks", null);
        } finally {
            lockService.release(multiLock);
        }
    }
}
