package com.example.catalog.transfer.theatre;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class TheatreSaveRequest {
    private String name;
    private Long cityId;
    private String addressLine;
    private String addressLine2;
    private String zipCode;
    private List<ScreenSaveRequest> screens;
}