package com.movie.backend.dto;

import lombok.Data;

@Data
public class MovieRequestDTO {
private String name;
private String language;
private String genre;
private int duration;
private double price;
}
