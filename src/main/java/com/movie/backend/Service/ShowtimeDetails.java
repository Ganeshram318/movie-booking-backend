package com.movie.backend.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.movie.backend.entities.ShowTime;

// Values derived from a showtime, with fallbacks to the movie for rows created before these fields existed
public final class ShowtimeDetails {

    private ShowtimeDetails() {
    }

    public static BigDecimal ticketPrice(ShowTime showtime) {
        BigDecimal price = showtime.getTicketPrice() != null
                ? showtime.getTicketPrice()
                : BigDecimal.valueOf(showtime.getMovie().getPrice());
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    public static String language(ShowTime showtime) {
        return showtime.getLanguage() != null && !showtime.getLanguage().isBlank()
                ? showtime.getLanguage()
                : showtime.getMovie().getLanguage();
    }

    public static boolean hasStarted(ShowTime showtime) {
        return !LocalDateTime.of(showtime.getShowDate(), showtime.getStartTime())
                .isAfter(LocalDateTime.now());
    }
}
