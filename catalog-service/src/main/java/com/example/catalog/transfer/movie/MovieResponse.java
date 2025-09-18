package com.example.catalog.transfer.movie;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieResponse {
    private Long id;
    private String systemCode;
    private String title;
    private int durationMin;
    private String language;
    private String genres;
    private String posterUrl;
    private double rating;
}
