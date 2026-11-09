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
public class ShowtimeRequestDto {

    private Integer movieId;

    private Integer theatreId;

    private LocalDate showDate;

    private LocalTime startTime;

    private Integer screenNumber;

    private Integer totalSeats;

    private String format;

    private String language;

    private BigDecimal ticketPrice;
}
