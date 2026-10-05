package com.movie.backend.dto;

import lombok.Data;

@Data
public class MovieResponseDto {
private Integer id;
private String name;
private String language;
private String genre;
private int duration;
private double price;
}
