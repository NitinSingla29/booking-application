package com.example.inventory.transfer.theatre;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScreenSaveRequest {
    private String name;
    private List<SeatDefinitionSaveRequest> seats;
}