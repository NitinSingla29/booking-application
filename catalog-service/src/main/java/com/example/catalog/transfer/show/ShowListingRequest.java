package com.example.catalog.transfer.show;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ShowListingRequest {
    private String city;

    private String movieTitle;

    private LocalDate movieDate;

    private int pageSize;

    private int pageNumber;

}
