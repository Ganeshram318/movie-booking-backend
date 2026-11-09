package com.movie.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movie.backend.entities.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<Booking> findByUserIdAndIdempotencyKey(String userId, String idempotencyKey);

    boolean existsByShowtimeId(Integer showtimeId);
}
