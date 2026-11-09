package com.movie.backend.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class MovieResponseDto {
private Integer id;
private String name;
private String language;
private String genre;
private int duration;
private double price;
private String description;
private LocalDate releaseDate;
private String posterUrl;
private String bannerUrl;
private double rating;
private boolean featured;
private String director;
private String cast;
private String certification;
}
