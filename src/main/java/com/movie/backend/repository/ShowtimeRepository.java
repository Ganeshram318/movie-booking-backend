
package com.movie.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.backend.entities.ShowTime;

@Repository
public interface ShowtimeRepository
        extends JpaRepository<ShowTime, Integer> {

}
