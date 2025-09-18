package com.example.catalog.repository.jpa;

import com.example.catalog.domain.jpa.SeatInventoryEntry;
import com.example.catalog.domain.jpa.Show;
import com.example.catalog.enumeration.SeatInventoryStatus;

import java.util.List;
import java.util.Optional;

public interface ISeatInventoryEntryRepository extends IRelationEntityRepository<SeatInventoryEntry> {
    Optional<SeatInventoryEntry> findBySystemCode(String systemCode);

    List<SeatInventoryEntry> findByShow(Show show);

    List<SeatInventoryEntry> findByShowAndSeatInventoryStatus(Show show, SeatInventoryStatus seatStatus);
}
