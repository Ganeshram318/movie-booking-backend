package com.movie.backend.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.movie.backend.dto.TheatreRequestDto;
import com.movie.backend.dto.TheatreResponseDto;

public interface TheatreService {
TheatreResponseDto addTheatre(TheatreRequestDto dto);

List<TheatreResponseDto> getAllTheatres();

TheatreResponseDto updateTheatre(Integer id, TheatreRequestDto dto);

void deleteTheatre(Integer id);
}
