package com.example.catalog.service;

import com.example.catalog.domain.jpa.ShowPriceRule;
import com.example.catalog.repository.jpa.IShowPriceRuleRepository;
import com.example.catalog.transfer.show.price.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShowPriceRuleService {

    @Autowired
    private IShowPriceRuleRepository showPriceRuleRepository;

    public ShowPriceRuleCreateResponse createShowPriceRule(ShowPriceRuleCreateRequest request) {
        ShowPriceRule rule = new ShowPriceRule(
                request.getShowSystemCode(),
                request.getSeatType(),
                request.getPrice(),
                request.getCurrency()
        );
        ShowPriceRule saved = showPriceRuleRepository.save(rule);
        return new ShowPriceRuleCreateResponse(saved.getId(), "ShowPriceRule created");
    }

    public ShowPriceRuleResponse getShowPriceRuleById(Long id) {
        return showPriceRuleRepository.findById(id)
                .map(this::toResponse)
                .orElse(new ShowPriceRuleResponse("ShowPriceRule not found"));
    }

    public ShowPriceRuleUpdateResponse updateShowPriceRule(ShowPriceRuleUpdateRequest request) {
        ShowPriceRule rule = showPriceRuleRepository
                .findByShowSystemCodeAndSeatType(request.getShowSystemCode(), request.getSeatType())
                .orElse(null);

        if (rule == null) {
            return new ShowPriceRuleUpdateResponse("ShowPriceRule not found");
        }

        rule.setPrice(request.getPrice());
        rule.setCurrency(request.getCurrency());

        ShowPriceRule saved = showPriceRuleRepository.save(rule);
        return new ShowPriceRuleUpdateResponse("ShowPriceRule updated");
    }


    public ShowPriceRuleDeleteResponse deleteShowPriceRule(ShowPriceRuleDeleteRequest request) {
        ShowPriceRule rule = showPriceRuleRepository
                .findByShowSystemCodeAndSeatType(request.getShowSystemCode(), request.getSeatType())
                .orElse(null);

        if (rule == null) {
            return new ShowPriceRuleDeleteResponse("ShowPriceRule not found");
        }

        showPriceRuleRepository.delete(rule);
        return new ShowPriceRuleDeleteResponse("ShowPriceRule deleted");
    }

    private ShowPriceRuleResponse toResponse(ShowPriceRule rule) {
        ShowPriceRuleResponse response = new ShowPriceRuleResponse();
        response.setShowSystemCode(rule.getShowSystemCode());
        response.setSeatType(rule.getSeatType());
        response.setPrice(rule.getPrice());
        response.setCurrency(rule.getCurrency());
        return response;
    }
}