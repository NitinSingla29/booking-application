package com.example.catalog.service;

import com.example.catalog.repository.jpa.IShowPriceRuleRepository;
import com.example.catalog.transfer.show.price.ShowPriceCalculationRequest;
import com.example.catalog.transfer.show.price.ShowPriceCalculationResponse;
import com.example.core.enumeration.OperationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ShowPricingService {

    @Autowired
    private IShowPriceRuleRepository showPriceRuleRepository;

    @Transactional(readOnly = true)
    public ShowPriceCalculationResponse calculateShowPrice(ShowPriceCalculationRequest request) {
        var ruleOpt = showPriceRuleRepository.findByShowSystemCodeAndSeatType(
                request.getShowSystemCode(),
                request.getSeatType()
        );

        if (ruleOpt.isPresent()) {
            var rule = ruleOpt.get();
            BigDecimal price = rule.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
            return new ShowPriceCalculationResponse(rule.getShowSystemCode(), price, rule.getCurrency());
        } else {
            return new ShowPriceCalculationResponse(OperationStatus.FAILURE, "ShowPriceRule not found");
        }
    }
}
