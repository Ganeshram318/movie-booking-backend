 package com.movie.backend.Service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.movie.backend.dto.MovieRequestDTO;
import com.movie.backend.dto.MovieResponseDto;
import com.movie.backend.entities.Movie;
import com.movie.backend.repository.MovieRepository;

@Service
public class MovieService {
private final MovieRepository movieRepository;
private final ModelMapper modelMapper;

public MovieService(MovieRepository movieRepository ,ModelMapper modelMapper) {
	this.movieRepository = movieRepository;
	this.modelMapper = modelMapper;
}

public List<MovieResponseDto> getallMovies(){
	 List<Movie> movies = movieRepository.findAll();
	 List<MovieResponseDto> responseDto = movies.stream()
	            .map(movie -> modelMapper.map(movie, MovieResponseDto.class))
	            .toList();
	 return responseDto;
}
public MovieResponseDto addMovie(MovieRequestDTO dto) {
	Movie movie = modelMapper.map(dto, Movie.class);
	
	Movie savedMovie = movieRepository.save(movie);
	
	MovieResponseDto responseDto = modelMapper.map(savedMovie, MovieResponseDto.class);
	
	return responseDto;
}
 
}
