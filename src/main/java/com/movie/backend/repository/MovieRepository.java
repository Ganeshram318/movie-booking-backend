package com.movie.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movie.backend.entities.Movie;

public interface MovieRepository extends JpaRepository<Movie, Integer>{

}
