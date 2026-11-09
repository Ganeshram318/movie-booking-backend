
package com.movie.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.movie.backend.Service.ShowtimeService;
import com.movie.backend.dto.ShowtimeRequestDto;
import com.movie.backend.dto.ShowtimeResponseDto;

import lombok.RequiredArgsConstructor;




@RestController
@RequestMapping("/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    // ADMIN - Add Showtime
    @PostMapping
    public ResponseEntity<ShowtimeResponseDto> addShowtime(
            @RequestBody ShowtimeRequestDto dto) {

        ShowtimeResponseDto response =
                showtimeService.addShowtime(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // USER / ADMIN - Get All Showtimes
    @GetMapping
    public ResponseEntity<List<ShowtimeResponseDto>>
            getAllShowtimes() {

        return ResponseEntity.ok(
                showtimeService.getAllShowtimes());
    }

    // USER / ADMIN - Get Showtime by ID
    @GetMapping("/{id}")
    public ResponseEntity<ShowtimeResponseDto>
            getShowtimeById(@PathVariable Integer id) {

        return ResponseEntity.ok(
                showtimeService.getShowtimeById(id));
    }

    // USER / ADMIN - Get Showtimes by Movie
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowtimeResponseDto>>
            getShowtimesByMovieId(
                    @PathVariable Integer movieId) {

        return ResponseEntity.ok(
                showtimeService.getShowtimesByMovieId(movieId));
    }

    // ADMIN - Update Showtime
    @PutMapping("/{id}")
    public ResponseEntity<ShowtimeResponseDto>
            updateShowtime(
                    @PathVariable Integer id,
                    @RequestBody ShowtimeRequestDto dto) {

        return ResponseEntity.ok(
                showtimeService.updateShowtime(id, dto));
    }

    // ADMIN - Delete Showtime
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShowtime(
            @PathVariable Integer id) {

        showtimeService.deleteShowtime(id);

        return ResponseEntity.noContent().build();
    }
}
