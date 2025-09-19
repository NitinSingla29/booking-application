package com.example.catalog.transfer.show.price;

import com.example.catalog.enumeration.SeatType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowPriceCalculationRequest {

    private String showSystemCode;

    private SeatType seatType;

    private int quantity;
}
