package com.example.catalog.transfer.theatre;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScreenResponse {
    private Long id;
    private String systemCode;
    private String name;
    private List<SeatDefinitionResponse> seats;
}