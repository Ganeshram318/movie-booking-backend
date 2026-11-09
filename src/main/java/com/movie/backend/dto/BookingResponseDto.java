package com.movie.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.Data;

@Data
public class BookingResponseDto {

    private Integer id;

    private String bookingReference;

    private Integer showtimeId;

    private Integer movieId;

    private String movieName;

    private Integer theatreId;

    private String theatreName;

    private String theatreCity;

    private String theatreAddress;

    private LocalDate showDate;

    private LocalTime startTime;

    private Integer screenNumber;

    private String format;

    private String language;

    private List<String> seats;

    private BigDecimal ticketPrice;

    private BigDecimal subtotal;

    private BigDecimal convenienceFee;

    private BigDecimal totalAmount;

    private String status;

    private String paymentStatus;

    private LocalDateTime createdAt;
}
