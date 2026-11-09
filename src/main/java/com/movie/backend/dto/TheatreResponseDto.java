package com.movie.backend.dto;

import lombok.Data;

@Data
public class TheatreResponseDto {

    private Integer id;

    private String name;

    private String city;

    private String address;

    private Integer totalscreens;
}