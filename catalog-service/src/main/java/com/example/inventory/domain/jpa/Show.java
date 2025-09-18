package com.example.inventory.domain.jpa;

import com.example.inventory.domain.jpa.base.SystemCodedRelationEntity;
import com.example.inventory.enumeration.ShowStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
@Getter
@Setter
@NoArgsConstructor
public class Show extends SystemCodedRelationEntity {

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movieId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    private Screen screen;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id")
    private Theatre theatreId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "show_date")
    private LocalDate showDate;

    @Column(name = "show_status")
    @Enumerated(EnumType.STRING)
    private ShowStatus showStatus;


    public Show(Movie movieId, Screen screen, Theatre theatreId, LocalDateTime startTime, LocalDateTime endTime, LocalDate showDate, ShowStatus showStatus) {
        this.movieId = movieId;
        this.screen = screen;
        this.theatreId = theatreId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.showDate = showDate;
        this.showStatus = showStatus;
    }
}
