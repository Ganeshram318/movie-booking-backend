package com.movie.backend.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.movie.backend.dto.BookingRequestDto;
import com.movie.backend.dto.BookingResponseDto;
import com.movie.backend.entities.BookedSeat;
import com.movie.backend.entities.Booking;
import com.movie.backend.entities.ShowTime;
import com.movie.backend.entities.User;
import com.movie.backend.enums.BookingStatus;
import com.movie.backend.enums.PaymentStatus;
import com.movie.backend.repository.BookedSeatRepository;
import com.movie.backend.repository.BookingRepository;
import com.movie.backend.repository.ShowtimeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    public static final int MAX_SEATS_PER_BOOKING = 10;

    private final BookingRepository bookingRepository;

    private final BookedSeatRepository bookedSeatRepository;

    private final ShowtimeRepository showtimeRepository;

    @Value("${booking.convenience-fee:40}")
    private BigDecimal convenienceFee;

    @Transactional
    public BookingResponseDto createBooking(User user, BookingRequestDto dto) {
        if (dto == null || dto.getShowtimeId() == null
                || dto.getSeats() == null || dto.getSeats().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Showtime and at least one seat are required");
        }

        String idempotencyKey = dto.getIdempotencyKey() == null
                || dto.getIdempotencyKey().isBlank() ? null : dto.getIdempotencyKey().trim();
        if (idempotencyKey != null && idempotencyKey.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Idempotency key is too long");
        }

        Set<String> seatLabels = new LinkedHashSet<>();
        for (String seat : dto.getSeats()) {
            if (seat != null) {
                seatLabels.add(seat.trim().toUpperCase(Locale.ROOT));
            }
        }
        if (seatLabels.size() != dto.getSeats().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Seats must be unique and non-empty");
        }
        if (seatLabels.size() > MAX_SEATS_PER_BOOKING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can book at most " + MAX_SEATS_PER_BOOKING + " seats at a time");
        }

        // Locking the showtime row makes concurrent bookings for this show run one at a time
        ShowTime showtime = showtimeRepository.findByIdForUpdate(dto.getShowtimeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Showtime not found"));

        if (idempotencyKey != null) {
            Booking existing = bookingRepository
                    .findByUserIdAndIdempotencyKey(user.getId(), idempotencyKey)
                    .orElse(null);
            if (existing != null) {
                if (!existing.getShowtime().getId().equals(showtime.getId())
                        || !Set.copyOf(seatLabels(existing)).equals(seatLabels)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "This booking request was already used for different seats");
                }
                return toDto(existing);
            }
        }

        if (ShowtimeDetails.hasStarted(showtime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "This show has already started");
        }

        for (String label : seatLabels) {
            if (!SeatLayout.isValidSeat(label, showtime.getTotalSeats())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Seat " + label + " does not exist for this show");
            }
        }

        List<String> taken = bookedSeatRepository.findTakenSeatLabels(showtime.getId(), seatLabels);
        if (!taken.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Seats already booked: " + String.join(", ", taken.stream().sorted().toList()));
        }

        BigDecimal ticketPrice = ShowtimeDetails.ticketPrice(showtime);
        BigDecimal subtotal = ticketPrice.multiply(BigDecimal.valueOf(seatLabels.size()));

        Booking booking = new Booking();
        booking.setBookingReference(newReference());
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setTicketPrice(ticketPrice);
        booking.setSubtotal(subtotal);
        BigDecimal fee = convenienceFee.setScale(2, RoundingMode.HALF_UP);
        booking.setConvenienceFee(fee);
        booking.setTotalAmount(subtotal.add(fee));
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentStatus(PaymentStatus.DEMO_NOT_CHARGED);
        booking.setIdempotencyKey(idempotencyKey);
        booking.setCreatedAt(LocalDateTime.now());

        for (String label : seatLabels) {
            BookedSeat seat = new BookedSeat();
            seat.setBooking(booking);
            seat.setShowtime(showtime);
            seat.setSeatLabel(label);
            booking.getSeats().add(seat);
        }

        // saveAndFlush surfaces unique-constraint violations here so they map to 409
        return toDto(bookingRepository.saveAndFlush(booking));
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getMyBookings(User user) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponseDto getBooking(User user, Integer id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Booking not found"));
        boolean isAdmin = user.getRole() != null && "ROLE_ADMIN".equals(user.getRole().name());
        if (!isAdmin && !booking.getUser().getId().equals(user.getId())) {
            // Same response as a missing booking so ids cannot be probed
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found");
        }
        return toDto(booking);
    }

    private static List<String> seatLabels(Booking booking) {
        List<String> labels = new ArrayList<>();
        booking.getSeats().forEach(seat -> labels.add(seat.getSeatLabel()));
        return labels;
    }

    private static String newReference() {
        return "STF" + UUID.randomUUID().toString().replace("-", "")
                .substring(0, 10).toUpperCase(Locale.ROOT);
    }

    private BookingResponseDto toDto(Booking booking) {
        ShowTime showtime = booking.getShowtime();
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setBookingReference(booking.getBookingReference());
        dto.setShowtimeId(showtime.getId());
        dto.setMovieId(showtime.getMovie().getId());
        dto.setMovieName(showtime.getMovie().getName());
        dto.setTheatreId(showtime.getTheatre().getId());
        dto.setTheatreName(showtime.getTheatre().getName());
        dto.setTheatreCity(showtime.getTheatre().getCity());
        dto.setTheatreAddress(showtime.getTheatre().getAddress());
        dto.setShowDate(showtime.getShowDate());
        dto.setStartTime(showtime.getStartTime());
        dto.setScreenNumber(showtime.getScreenNumber());
        dto.setFormat(showtime.getFormat());
        dto.setLanguage(ShowtimeDetails.language(showtime));
        dto.setSeats(seatLabels(booking).stream().sorted(SeatLayoutOrder.INSTANCE).toList());
        dto.setTicketPrice(booking.getTicketPrice());
        dto.setSubtotal(booking.getSubtotal());
        dto.setConvenienceFee(booking.getConvenienceFee());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setStatus(booking.getStatus().name());
        dto.setPaymentStatus(booking.getPaymentStatus().name());
        dto.setCreatedAt(booking.getCreatedAt());
        return dto;
    }

    private enum SeatLayoutOrder implements java.util.Comparator<String> {
        INSTANCE;

        @Override
        public int compare(String a, String b) {
            int row = Character.compare(a.charAt(0), b.charAt(0));
            return row != 0 ? row
                    : Integer.compare(Integer.parseInt(a.substring(1)), Integer.parseInt(b.substring(1)));
        }
    }
}
