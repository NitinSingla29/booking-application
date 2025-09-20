package com.example.catalog.transfer.theatre;

import com.example.booking.enumeration.SeatType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatDefinitionSaveRequest {
    private String seatCode;
    private SeatType seatType;
    private int rowNumber;
    private int columnNumber;

}