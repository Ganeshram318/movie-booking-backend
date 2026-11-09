package com.movie.backend.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "showtime")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id", nullable = false)
    private Theatre theatre;

    private LocalDate showDate;

    private LocalTime startTime;

    private Integer screenNumber;

    private Integer totalSeats;

    // Nullable so existing rows stay valid; null falls back to the movie value
    @Column(length = 20)
    private String format;

    @Column(length = 50)
    private String language;

    @Column(precision = 10, scale = 2)
    private BigDecimal ticketPrice;
}
