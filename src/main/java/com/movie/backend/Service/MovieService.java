 package com.movie.backend.Service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.movie.backend.dto.MovieRequestDto;
import com.movie.backend.dto.MovieRequestDto;
import com.movie.backend.dto.MovieResponseDto;
import com.movie.backend.entities.Movie;
import com.movie.backend.repository.MovieRepository;
import com.movie.backend.repository.ShowtimeRepository;

@Service
public class MovieService {
private final MovieRepository movieRepository;
private final ModelMapper modelMapper;
private final ShowtimeRepository showtimeRepository;

public MovieService(MovieRepository movieRepository ,ModelMapper modelMapper, ShowtimeRepository showtimeRepository) {
	this.movieRepository = movieRepository;
	this.modelMapper = modelMapper;
	this.showtimeRepository = showtimeRepository;
}

public MovieResponseDto getMovieById(Integer id) {
	return modelMapper.map(findMovie(id), MovieResponseDto.class);
}

private Movie findMovie(Integer id) {
	return movieRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
}

public List<MovieResponseDto> getallMovies(){
	 List<Movie> movies = movieRepository.findAll();
	 List<MovieResponseDto> responseDto = movies.stream()
	            .map(movie -> modelMapper.map(movie, MovieResponseDto.class))
	            .toList();
	 return responseDto;
}
public MovieResponseDto addMovie(MovieRequestDto dto) {
	Movie movie = modelMapper.map(dto, Movie.class);
	
	Movie savedMovie = movieRepository.save(movie);
	
	MovieResponseDto responseDto = modelMapper.map(savedMovie, MovieResponseDto.class);
	
	return responseDto;
}

public MovieResponseDto updateMovie(Integer id, MovieRequestDto dto) {
	Movie movie = findMovie(id);
	modelMapper.map(dto, movie);
	Movie updateMovie = movieRepository.save(movie);
	return modelMapper.map(updateMovie, MovieResponseDto.class);
}

public void deleteMovie(Integer id) {
	Movie movie = findMovie(id);
	if (showtimeRepository.existsByMovieId(id)) {
		throw new ResponseStatusException(HttpStatus.CONFLICT,
				"This movie has showtimes. Delete its showtimes before deleting the movie.");
	}

	 movieRepository.delete(movie);
}
 
}
