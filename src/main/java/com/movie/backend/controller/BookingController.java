package com.movie.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.backend.Service.BookingService;
import com.movie.backend.dto.BookingRequestDto;
import com.movie.backend.dto.BookingResponseDto;
import com.movie.backend.entities.User;

import lombok.RequiredArgsConstructor;

// Requires an authenticated user (covered by the anyRequest().authenticated() rule)
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(
            @AuthenticationPrincipal User user,
            @RequestBody BookingRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(user, dto));
    }

    @GetMapping("/my")
    public List<BookingResponseDto> getMyBookings(@AuthenticationPrincipal User user) {
        return bookingService.getMyBookings(user);
    }

    @GetMapping("/{id}")
    public BookingResponseDto getBooking(
            @AuthenticationPrincipal User user,
            @PathVariable Integer id) {
        return bookingService.getBooking(user, id);
    }
}
