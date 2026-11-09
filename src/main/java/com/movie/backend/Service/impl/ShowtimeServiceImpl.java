
package com.movie.backend.Service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.movie.backend.Service.ShowtimeService;
import com.movie.backend.dto.ShowtimeRequestDto;
import com.movie.backend.dto.ShowtimeResponseDto;
import com.movie.backend.entities.Movie;
import com.movie.backend.entities.ShowTime;
import com.movie.backend.entities.Theatre;
import com.movie.backend.entities.ShowTime;
import com.movie.backend.repository.MovieRepository;
import com.movie.backend.repository.TheatreRepository;
import com.movie.backend.repository.ShowtimeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;

    @Override
    public ShowtimeResponseDto addShowtime(
            ShowtimeRequestDto dto) {

        ShowTime showtime = new ShowTime();

        setShowtimeDetails(showtime, dto);

        ShowTime saved = showtimeRepository.save(showtime);

        return convertToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeResponseDto> getAllShowtimes() {

        return showtimeRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ShowtimeResponseDto getShowtimeById(Integer id) {

        ShowTime showtime = findShowtime(id);

        return convertToDto(showtime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeResponseDto> getShowtimesByMovieId(
            Integer movieId) {

        findMovie(movieId);

        return showtimeRepository.findAll()
                .stream()
                .filter(showtime ->
                        showtime.getMovie().getId()
                                .equals(movieId))
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public ShowtimeResponseDto updateShowtime(
            Integer id,
            ShowtimeRequestDto dto) {

        ShowTime showtime = findShowtime(id);

        setShowtimeDetails(showtime, dto);

        ShowTime updated = showtimeRepository.save(showtime);

        return convertToDto(updated);
    }

    @Override
    public void deleteShowtime(Integer id) {

        ShowTime showtime = findShowtime(id);

        showtimeRepository.delete(showtime);
    }

    private ShowTime findShowtime(Integer id) {

        return showtimeRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Showtime not found"));
    }

    private Movie findMovie(Integer id) {

        return movieRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Movie not found"));
    }

    private Theatre findTheatre(Integer id) {

        return theatreRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Theatre not found"));
    }

    private void setShowtimeDetails(
            ShowTime showtime,
            ShowtimeRequestDto dto) {

        if (dto == null
                || dto.getMovieId() == null
                || dto.getTheatreId() == null
                || dto.getShowDate() == null
                || dto.getStartTime() == null
                || dto.getScreenNumber() == null
                || dto.getTotalSeats() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "All showtime fields are required");
        }

        if (dto.getScreenNumber() < 1
                || dto.getTotalSeats() < 1) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Screen number and total seats must be positive");
        }

        Movie movie = findMovie(dto.getMovieId());
        Theatre theatre = findTheatre(dto.getTheatreId());

        if (theatre.getTotalscreens() == null
                || dto.getScreenNumber() > theatre.getTotalscreens()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid screen number for this theatre");
        }

        showtime.setMovie(movie);
        showtime.setTheatre(theatre);
        showtime.setShowDate(dto.getShowDate());
        showtime.setStartTime(dto.getStartTime());
        showtime.setScreenNumber(dto.getScreenNumber());
        showtime.setTotalSeats(dto.getTotalSeats());
    }

    private ShowtimeResponseDto convertToDto(
            ShowTime showtime) {

        ShowtimeResponseDto dto = new ShowtimeResponseDto();

        dto.setId(showtime.getId());

        dto.setMovieId(showtime.getMovie().getId());
        dto.setMovieName(showtime.getMovie().getName());

        dto.setTheatreId(showtime.getTheatre().getId());
        dto.setTheatreName(showtime.getTheatre().getName());

        dto.setShowDate(showtime.getShowDate());
        dto.setStartTime(showtime.getStartTime());
        dto.setScreenNumber(showtime.getScreenNumber());
        dto.setTotalSeats(showtime.getTotalSeats());

        return dto;
    }
}

