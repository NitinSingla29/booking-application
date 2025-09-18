package com.example.inventory.repository.jpa;

import com.example.inventory.domain.jpa.SeatInventoryEntry;
import com.example.inventory.domain.jpa.Show;
import com.example.inventory.enumeration.SeatInventoryStatus;

import java.util.List;
import java.util.Optional;

public interface ISeatInventoryEntryRepository extends IRelationEntityRepository<SeatInventoryEntry> {
    Optional<SeatInventoryEntry> findBySystemCode(String systemCode);

    List<SeatInventoryEntry> findByShow(Show show);

    List<SeatInventoryEntry> findByShowAndSeatInventoryStatus(Show show, SeatInventoryStatus seatStatus);
}
