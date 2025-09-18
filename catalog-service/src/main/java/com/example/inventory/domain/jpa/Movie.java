package com.example.inventory.domain.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movie")
@Getter
@Setter
@NoArgsConstructor
public class Movie extends SystemCodedRelationEntity {

    @Column(name = "title")
    private String title;

    @Column(name = "duration_min")
    private int durationMin;

    @Column(name = "language")
    private String language;

    @Column(name = "genres")
    private String genres;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "rating")
    private double rating;

    public Movie(String title, int durationMin, String language, String genres) {
        this.title = title;
        this.durationMin = durationMin;
        this.language = language;
        this.genres = genres;
    }
}
