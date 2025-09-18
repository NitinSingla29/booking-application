package com.example.inventory.domain.jpa;

import com.example.inventory.domain.jpa.base.SystemCodedRelationEntity;
import com.example.inventory.enumeration.ShowStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Show extends SystemCodedRelationEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    private Screen screen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id")
    private Theatre theatre;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "show_date")
    private LocalDate showDate;

    @Column(name = "show_status")
    @Enumerated(EnumType.STRING)
    private ShowStatus showStatus;


    public Show(Movie movie, Screen screen, Theatre theatre, LocalDateTime startTime, LocalDateTime endTime, LocalDate showDate, ShowStatus showStatus) {
        Assert.notNull(movie, "Argument [movie] must be present");
        Assert.notNull(screen, "Argument [screen] must be present");
        Assert.notNull(theatre, "Argument [theatre] must be present");
        this.movie = movie;
        this.screen = screen;
        this.theatre = theatre;
        this.startTime = startTime;
        this.endTime = endTime;
        this.showDate = showDate;
        this.showStatus = showStatus;
    }
}
