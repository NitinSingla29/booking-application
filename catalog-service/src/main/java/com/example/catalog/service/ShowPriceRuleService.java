package com.example.catalog.service;

import com.example.catalog.domain.jpa.ShowPriceRule;
import com.example.catalog.repository.jpa.IShowPriceRuleRepository;
import com.example.catalog.transfer.show.price.*;
import com.example.common.enumeration.OperationStatus;
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
        ShowPriceRule savedRule = showPriceRuleRepository.save(rule);
        return new ShowPriceRuleCreateResponse(savedRule.getSystemCode(), OperationStatus.SUCCESS, "ShowPriceRule created");
    }

    public ShowPriceRuleResponse getShowPriceRule(String ruleSystemCode) {
        return showPriceRuleRepository.findBySystemCode(ruleSystemCode)
                .map(this::toResponse)
                .orElse(new ShowPriceRuleResponse(OperationStatus.FAILURE, "ShowPriceRule not found"));
    }

    public ShowPriceRuleUpdateResponse updateShowPriceRule(ShowPriceRuleUpdateRequest request) {
        ShowPriceRule rule = showPriceRuleRepository
                .findBySystemCode(request.getRuleSystemCode())
                .orElse(null);

        if (rule == null) {
            return new ShowPriceRuleUpdateResponse(OperationStatus.FAILURE, "ShowPriceRule not found");
        }
        rule.setShowSystemCode(request.getShowSystemCode());
        rule.setSeatType(request.getSeatType());
        rule.setPrice(request.getPrice());
        rule.setCurrency(request.getCurrency());

        ShowPriceRule saved = showPriceRuleRepository.save(rule);
        return new ShowPriceRuleUpdateResponse(OperationStatus.SUCCESS, "ShowPriceRule updated");
    }


    public ShowPriceRuleDeleteResponse deleteShowPriceRule(String ruleSystemCode) {
        ShowPriceRule rule = showPriceRuleRepository.findBySystemCode(ruleSystemCode)
                .orElse(null);

        if (rule == null) {
            return new ShowPriceRuleDeleteResponse(OperationStatus.FAILURE, "ShowPriceRule not found");
        }

        showPriceRuleRepository.delete(rule);
        return new ShowPriceRuleDeleteResponse(OperationStatus.SUCCESS, "ShowPriceRule deleted");
    }

    private ShowPriceRuleResponse toResponse(ShowPriceRule rule) {
        ShowPriceRuleResponse response = new ShowPriceRuleResponse();
        response.setRuleSystemCode(rule.getSystemCode());
        response.setShowSystemCode(rule.getShowSystemCode());
        response.setSeatType(rule.getSeatType());
        response.setPrice(rule.getPrice());
        response.setCurrency(rule.getCurrency());
        return response;
    }
}