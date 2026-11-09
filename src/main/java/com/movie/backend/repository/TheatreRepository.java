package com.movie.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movie.backend.entities.Theatre;

public interface TheatreRepository extends JpaRepository<Theatre, Integer> {

}
