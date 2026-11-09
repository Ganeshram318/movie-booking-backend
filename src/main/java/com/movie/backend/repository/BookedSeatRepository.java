package com.movie.backend.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.movie.backend.entities.BookedSeat;

public interface BookedSeatRepository extends JpaRepository<BookedSeat, Integer> {

    @Query("select b.seatLabel from BookedSeat b where b.showtime.id = :showtimeId")
    List<String> findSeatLabelsByShowtimeId(@Param("showtimeId") Integer showtimeId);

    @Query("select b.seatLabel from BookedSeat b where b.showtime.id = :showtimeId and b.seatLabel in :labels")
    List<String> findTakenSeatLabels(@Param("showtimeId") Integer showtimeId,
            @Param("labels") Collection<String> labels);

    long countByShowtimeId(Integer showtimeId);

    @Query("select b.showtime.id, count(b) from BookedSeat b where b.showtime.id in :showtimeIds group by b.showtime.id")
    List<Object[]> countByShowtimeIds(@Param("showtimeIds") Collection<Integer> showtimeIds);
}
