package com.movie.backend.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatMapResponseDto {

    private Integer showtimeId;

    private Integer totalSeats;

    private Integer availableSeats;

    private BigDecimal ticketPrice;

    private BigDecimal convenienceFee;

    private List<SeatRowDto> rows;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SeatRowDto {
        private String row;
        private String category;
        private BigDecimal price;
        private List<SeatDto> seats;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SeatDto {
        private String id;
        private Integer number;
        private String status;
    }
}
