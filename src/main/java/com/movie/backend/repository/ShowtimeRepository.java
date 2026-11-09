package com.movie.backend.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.movie.backend.entities.ShowTime;

import jakarta.persistence.LockModeType;

@Repository
public interface ShowtimeRepository
        extends JpaRepository<ShowTime, Integer> {

    List<ShowTime> findByMovieIdOrderByShowDateAscStartTimeAsc(Integer movieId);

    boolean existsByMovieId(Integer movieId);

    boolean existsByTheatreId(Integer theatreId);

    @Query("select count(s) > 0 from ShowTime s where s.theatre.id = :theatreId"
            + " and s.screenNumber = :screenNumber and s.showDate = :showDate"
            + " and s.startTime = :startTime and (:excludeId is null or s.id <> :excludeId)")
    boolean existsSlot(@Param("theatreId") Integer theatreId,
            @Param("screenNumber") Integer screenNumber,
            @Param("showDate") LocalDate showDate,
            @Param("startTime") LocalTime startTime,
            @Param("excludeId") Integer excludeId);

    // Serializes concurrent bookings for the same show
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ShowTime s where s.id = :id")
    Optional<ShowTime> findByIdForUpdate(@Param("id") Integer id);
}
