package com.movie.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowtimeResponseDto {

    private Integer id;

    private Integer movieId;

    private String movieName;

    private Integer theatreId;

    private String theatreName;

    private String theatreCity;

    private String theatreAddress;

    private LocalDate showDate;

    private LocalTime startTime;

    private Integer screenNumber;

    private Integer totalSeats;

    private String format;

    private String language;

    private BigDecimal ticketPrice;

    private Integer bookedSeats;

    private Integer availableSeats;
}
