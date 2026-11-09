package com.movie.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.backend.Service.MovieService;
import com.movie.backend.dto.MovieRequestDto;
import com.movie.backend.dto.MovieResponseDto;
import com.movie.backend.entities.Movie;
@RestController
@RequestMapping("/movies")
@CrossOrigin

public class MovieController {
private final MovieService movieService;
public MovieController(MovieService movieService) {
	this.movieService = movieService;
}
@GetMapping
public List<MovieResponseDto> getallMovies(){
	return movieService.getallMovies();
}
@GetMapping("/{id}")
public MovieResponseDto getMovieById(@PathVariable Integer id) {
	return movieService.getMovieById(id);
}
@PostMapping
public MovieResponseDto addMovie(@RequestBody MovieRequestDto dto) {
	return movieService.addMovie(dto);
}
@PutMapping("/{id}")
public MovieResponseDto updateMovie(
		@PathVariable Integer id,
		@RequestBody MovieRequestDto dto){
			return movieService.updateMovie(id, dto);
		}

@DeleteMapping("/{id}")
public void deleteMovie(@PathVariable Integer id) {
	movieService.deleteMovie(id);
}

}
