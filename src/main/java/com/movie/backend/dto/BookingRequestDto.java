package com.movie.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {

    private Integer showtimeId;

    private List<String> seats;

    private String idempotencyKey;
}
