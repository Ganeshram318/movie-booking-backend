package com.movie.backend.Service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.movie.backend.Service.SeatLayout;
import com.movie.backend.Service.ShowtimeDetails;
import com.movie.backend.Service.ShowtimeService;
import com.movie.backend.dto.SeatMapResponseDto;
import com.movie.backend.dto.ShowtimeRequestDto;
import com.movie.backend.dto.ShowtimeResponseDto;
import com.movie.backend.entities.Movie;
import com.movie.backend.entities.ShowTime;
import com.movie.backend.entities.Theatre;
import com.movie.backend.repository.BookedSeatRepository;
import com.movie.backend.repository.BookingRepository;
import com.movie.backend.repository.MovieRepository;
import com.movie.backend.repository.ShowtimeRepository;
import com.movie.backend.repository.TheatreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;

    private final MovieRepository movieRepository;

    private final TheatreRepository theatreRepository;

    private final BookedSeatRepository bookedSeatRepository;

    private final BookingRepository bookingRepository;

    @Value("${booking.convenience-fee:40}")
    private BigDecimal convenienceFee;

    @Override
    public ShowtimeResponseDto addShowtime(
            ShowtimeRequestDto dto) {

        ShowTime showtime = new ShowTime();
        setShowtimeDetails(showtime, dto);

        if (ShowtimeDetails.hasStarted(showtime)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Show date and time must be in the future");
        }

        ShowTime saved = showtimeRepository.save(showtime);
        return convertToDto(saved, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeResponseDto> getAllShowtimes() {
        return convertAll(showtimeRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ShowtimeResponseDto getShowtimeById(Integer id) {
        ShowTime showtime = findShowtime(id);
        return convertToDto(showtime,
                bookedSeatRepository.countByShowtimeId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeResponseDto> getShowtimesByMovieId(
            Integer movieId) {

        findMovie(movieId);
        return convertAll(showtimeRepository
                .findByMovieIdOrderByShowDateAscStartTimeAsc(movieId));
    }

    @Override
    public ShowtimeResponseDto updateShowtime(
            Integer id,
            ShowtimeRequestDto dto) {

        ShowTime showtime = findShowtime(id);
        Integer previousMovieId = showtime.getMovie().getId();
        Integer previousTotalSeats = showtime.getTotalSeats();

        setShowtimeDetails(showtime, dto);

        if (bookingRepository.existsByShowtimeId(id)
                && (!previousMovieId.equals(dto.getMovieId())
                        || dto.getTotalSeats() < previousTotalSeats)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This show already has bookings; its movie cannot be changed and seats cannot be reduced");
        }

        ShowTime updated = showtimeRepository.save(showtime);
        return convertToDto(updated,
                bookedSeatRepository.countByShowtimeId(id));
    }

    @Override
    public void deleteShowtime(Integer id) {
        ShowTime showtime = findShowtime(id);

        if (bookingRepository.existsByShowtimeId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This show has bookings and cannot be deleted");
        }

        showtimeRepository.delete(showtime);
    }

    @Override
    @Transactional(readOnly = true)
    public SeatMapResponseDto getSeatMap(Integer id) {
        ShowTime showtime = findShowtime(id);
        Set<String> booked = new HashSet<>(
                bookedSeatRepository.findSeatLabelsByShowtimeId(id));
        BigDecimal price = ShowtimeDetails.ticketPrice(showtime);

        List<SeatMapResponseDto.SeatRowDto> rows = SeatLayout
                .rows(showtime.getTotalSeats())
                .stream()
                .map(row -> new SeatMapResponseDto.SeatRowDto(
                        row.label(),
                        row.category(),
                        price,
                        IntStream.rangeClosed(1, row.seatCount())
                                .mapToObj(number -> {
                                    String label = row.label() + number;
                                    return new SeatMapResponseDto.SeatDto(
                                            label,
                                            number,
                                            booked.contains(label) ? "BOOKED" : "AVAILABLE");
                                })
                                .toList()))
                .toList();

        return new SeatMapResponseDto(
                showtime.getId(),
                showtime.getTotalSeats(),
                Math.max(0, showtime.getTotalSeats() - booked.size()),
                price,
                convenienceFee.setScale(2, RoundingMode.HALF_UP),
                rows);
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

        if (dto.getTotalSeats() > SeatLayout.MAX_SEATS) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Total seats cannot exceed " + SeatLayout.MAX_SEATS);
        }

        if (dto.getTicketPrice() != null
                && dto.getTicketPrice().signum() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ticket price must be greater than zero");
        }

        String format = trimToNull(dto.getFormat());
        String language = trimToNull(dto.getLanguage());
        if ((format != null && format.length() > 20)
                || (language != null && language.length() > 50)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Format or language is too long");
        }

        Movie movie = findMovie(dto.getMovieId());
        Theatre theatre = findTheatre(dto.getTheatreId());

        if (theatre.getTotalscreens() == null
                || dto.getScreenNumber() > theatre.getTotalscreens()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid screen number for this theatre");
        }

        if (showtimeRepository.existsSlot(theatre.getId(),
                dto.getScreenNumber(), dto.getShowDate(),
                dto.getStartTime(), showtime.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Another show is already scheduled on this screen at the same date and time");
        }

        showtime.setMovie(movie);
        showtime.setTheatre(theatre);
        showtime.setShowDate(dto.getShowDate());
        showtime.setStartTime(dto.getStartTime());
        showtime.setScreenNumber(dto.getScreenNumber());
        showtime.setTotalSeats(dto.getTotalSeats());
        showtime.setFormat(format);
        showtime.setLanguage(language);
        showtime.setTicketPrice(dto.getTicketPrice());
    }

    private static String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private List<ShowtimeResponseDto> convertAll(List<ShowTime> showtimes) {
        if (showtimes.isEmpty()) {
            return List.of();
        }
        Map<Integer, Long> bookedCounts = new HashMap<>();
        for (Object[] row : bookedSeatRepository.countByShowtimeIds(
                showtimes.stream().map(ShowTime::getId).toList())) {
            bookedCounts.put((Integer) row[0], (Long) row[1]);
        }
        return showtimes.stream()
                .map(showtime -> convertToDto(showtime,
                        bookedCounts.getOrDefault(showtime.getId(), 0L)))
                .toList();
    }

    private ShowtimeResponseDto convertToDto(
            ShowTime showtime, long bookedSeats) {

        ShowtimeResponseDto dto = new ShowtimeResponseDto();

        dto.setId(showtime.getId());
        dto.setMovieId(showtime.getMovie().getId());
        dto.setMovieName(showtime.getMovie().getName());
        dto.setTheatreId(showtime.getTheatre().getId());
        dto.setTheatreName(showtime.getTheatre().getName());
        dto.setTheatreCity(showtime.getTheatre().getCity());
        dto.setTheatreAddress(showtime.getTheatre().getAddress());
        dto.setShowDate(showtime.getShowDate());
        dto.setStartTime(showtime.getStartTime());
        dto.setScreenNumber(showtime.getScreenNumber());
        dto.setTotalSeats(showtime.getTotalSeats());
        dto.setFormat(showtime.getFormat());
        dto.setLanguage(ShowtimeDetails.language(showtime));
        dto.setTicketPrice(ShowtimeDetails.ticketPrice(showtime));
        dto.setBookedSeats((int) bookedSeats);
        dto.setAvailableSeats(Math.max(0,
                showtime.getTotalSeats() - (int) bookedSeats));

        return dto;
    }
}
