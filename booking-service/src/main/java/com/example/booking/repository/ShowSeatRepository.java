package com.example.booking.repository;

import com.example.booking.entity.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, UUID> {
    List<ShowSeat> findByShowIdAndSeatNumberIn(UUID showId, java.util.List<String> seatNumbers);
}
