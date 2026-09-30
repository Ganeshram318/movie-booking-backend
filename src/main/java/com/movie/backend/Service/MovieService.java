 package com.movie.backend.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.movie.backend.dto.MovieRequestDTO;
import com.movie.backend.entities.Movie;
import com.movie.backend.repository.MovieRepository;

@Service
public class MovieService {
private final MovieRepository movieRepository;

public MovieService(MovieRepository movieRepository) {
	this.movieRepository = movieRepository;
}

public List<Movie> getallMovies(){
	return movieRepository.findAll();
}
public Movie addMovie(MovieRequestDTO dto) {
	Movie movie = new Movie();
	movie.setName(dto.getName());
	movie.setLanguage(dto.getLanguage());
	movie.setGenre(dto.getGenre());
	movie.setDuration(dto.getDuration());
	movie.setPrice(dto.getPrice());
	
	return movieRepository.save(movie);
}
}
