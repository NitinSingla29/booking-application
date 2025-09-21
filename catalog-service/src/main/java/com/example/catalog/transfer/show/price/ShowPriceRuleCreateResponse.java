package com.example.catalog.transfer.show.price;

import com.example.common.enumeration.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShowPriceRuleCreateResponse {
    private String ruleSystemCode;
    private OperationStatus status;
    private String message;
}