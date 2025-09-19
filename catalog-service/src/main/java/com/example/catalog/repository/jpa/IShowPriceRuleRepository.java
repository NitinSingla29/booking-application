package com.example.catalog.repository.jpa;

import com.example.catalog.domain.jpa.ShowPriceRule;
import com.example.catalog.enumeration.SeatType;
import com.example.catalog.repository.jpa.base.IRelationEntityRepository;

import java.util.Optional;

public interface IShowPriceRuleRepository extends IRelationEntityRepository<ShowPriceRule> {

    Optional<ShowPriceRule> findBySystemCode(String systemCode);
    
    Optional<ShowPriceRule> findByShowSystemCodeAndSeatType(String showSystemCode, SeatType seatType);
}
