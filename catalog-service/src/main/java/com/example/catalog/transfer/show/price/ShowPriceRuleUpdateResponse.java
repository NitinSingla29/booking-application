package com.example.catalog.transfer.show.price;

import com.example.catalog.enumeration.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowPriceRuleUpdateResponse {
    private OperationStatus status;
    private String message;
}