package com.example.inventory.transfer.theatre;

import com.example.inventory.enumeration.SeatType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatDefinitionResponse {
    private Long id;
    private String systemCode;
    private String seatCode;
    private SeatType seatType;
    private int rowNumber;
    private int columnNumber;
}
