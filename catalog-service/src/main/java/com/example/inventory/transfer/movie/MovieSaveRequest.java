package com.example.inventory.transfer.movie;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieSaveRequest {
    private String title;
    private int durationMin;
    private String language;
    private String genres;
    private String posterUrl;
    private double rating;
}