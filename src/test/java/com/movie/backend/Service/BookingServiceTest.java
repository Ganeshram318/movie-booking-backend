package com.movie.backend.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import com.movie.backend.dto.BookingRequestDto;
import com.movie.backend.dto.BookingResponseDto;
import com.movie.backend.entities.Booking;
import com.movie.backend.entities.Movie;
import com.movie.backend.entities.ShowTime;
import com.movie.backend.entities.Theatre;
import com.movie.backend.entities.User;
import com.movie.backend.repository.BookedSeatRepository;
import com.movie.backend.repository.BookingRepository;
import com.movie.backend.repository.ShowtimeRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookedSeatRepository bookedSeatRepository;

    @Mock
    private ShowtimeRepository showtimeRepository;

    @InjectMocks
    private BookingService bookingService;

    private User user;

    private ShowTime showtime;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(bookingService, "convenienceFee", new BigDecimal("40"));

        user = new User();
        user.setId("user-1");

        Movie movie = new Movie();
        movie.setId(1);
        movie.setName("Leo");
        movie.setLanguage("Tamil");
        movie.setPrice(200);

        Theatre theatre = new Theatre();
        theatre.setId(1);
        theatre.setName("PVR");

        showtime = new ShowTime();
        showtime.setId(10);
        showtime.setMovie(movie);
        showtime.setTheatre(theatre);
        showtime.setShowDate(LocalDate.now().plusDays(1));
        showtime.setStartTime(LocalTime.NOON);
        showtime.setScreenNumber(1);
        showtime.setTotalSeats(24);
    }

    @Test
    void calculatesTotalsOnServerUsingMoviePriceFallback() {
        when(showtimeRepository.findByIdForUpdate(10)).thenReturn(Optional.of(showtime));
        when(bookedSeatRepository.findTakenSeatLabels(eq(10), anyCollection())).thenReturn(List.of());
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingResponseDto result = bookingService.createBooking(user,
                new BookingRequestDto(10, List.of("b2", "A1"), null));

        assertEquals(new BigDecimal("400.00"), result.getSubtotal());
        assertEquals(new BigDecimal("440.00"), result.getTotalAmount());
        assertEquals(List.of("A1", "B2"), result.getSeats());
        assertEquals("DEMO_NOT_CHARGED", result.getPaymentStatus());
    }

    @Test
    void usesShowtimeTicketPriceWhenSet() {
        showtime.setTicketPrice(new BigDecimal("320"));
        when(showtimeRepository.findByIdForUpdate(10)).thenReturn(Optional.of(showtime));
        when(bookedSeatRepository.findTakenSeatLabels(eq(10), anyCollection())).thenReturn(List.of());
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingResponseDto result = bookingService.createBooking(user,
                new BookingRequestDto(10, List.of("A1"), null));

        assertEquals(new BigDecimal("360.00"), result.getTotalAmount());
    }

    @Test
    void rejectsAlreadyBookedSeatsWithConflict() {
        when(showtimeRepository.findByIdForUpdate(10)).thenReturn(Optional.of(showtime));
        when(bookedSeatRepository.findTakenSeatLabels(eq(10), anyCollection())).thenReturn(List.of("A1"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> bookingService.createBooking(user, new BookingRequestDto(10, List.of("A1"), null)));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(bookingRepository, never()).saveAndFlush(any());
    }

    @Test
    void rejectsSeatsOutsideShowCapacity() {
        when(showtimeRepository.findByIdForUpdate(10)).thenReturn(Optional.of(showtime));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> bookingService.createBooking(user, new BookingRequestDto(10, List.of("C1"), null)));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void rejectsShowsThatHaveStarted() {
        showtime.setShowDate(LocalDate.now().minusDays(1));
        when(showtimeRepository.findByIdForUpdate(10)).thenReturn(Optional.of(showtime));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> bookingService.createBooking(user, new BookingRequestDto(10, List.of("A1"), null)));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}
