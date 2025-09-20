package com.example.booking.client.transfer.pricing;

import com.example.booking.enumeration.SeatType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowPriceCalculationRequest {

    private String userSystemCode;

    private String showSystemCode;

    private SeatType seatType;

    private int quantity;
}
