package com.example.catalog.transfer.show.price;

import com.example.core.enumeration.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShowPriceRuleDeleteResponse {
    private OperationStatus status;
    private String message;
}