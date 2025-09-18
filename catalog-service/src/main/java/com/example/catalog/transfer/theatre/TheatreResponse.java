package com.example.catalog.transfer.theatre;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TheatreResponse {
    private Long id;
    private String systemCode;
    private String name;
    private String addressLine;
    private String addressLine2;
    private String zipCode;
    private Long cityId;
    private List<ScreenResponse> screens;
}
