
package com.movie.backend.Service;

import java.util.List;

import com.movie.backend.dto.ShowtimeRequestDto;
import com.movie.backend.dto.ShowtimeResponseDto;

public interface ShowtimeService {

    ShowtimeResponseDto addShowtime(
            ShowtimeRequestDto requestDto);

    List<ShowtimeResponseDto> getAllShowtimes();

    ShowtimeResponseDto getShowtimeById(
            Integer id);

    List<ShowtimeResponseDto> getShowtimesByMovieId(
            Integer movieId);

    ShowtimeResponseDto updateShowtime(
            Integer id,
            ShowtimeRequestDto requestDto);

    void deleteShowtime(Integer id);
}
