package com.movie.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.backend.Service.MovieService;
import com.movie.backend.dto.MovieRequestDTO;
import com.movie.backend.dto.MovieResponseDto;
import com.movie.backend.entities.Movie;
@RestController
@RequestMapping("/movies")
public class MovieController {
private final MovieService movieService;
public MovieController(MovieService movieService) {
	this.movieService = movieService;
}
@GetMapping
public List<MovieResponseDto> getallMovies(){
	return movieService.getallMovies();
}
@PostMapping
public MovieResponseDto addMovie(@RequestBody MovieRequestDTO dto) {
	return movieService.addMovie(dto);
}

}
