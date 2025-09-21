package com.example.catalog.transfer.show.price;

import com.example.common.enumeration.SeatType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShowPriceCalculationRequest {

    private String userSystemCode;

    private String showSystemCode;

    private SeatType seatType;

    private int quantity;
}
