package com.movie.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movie.backend.entities.User;

public interface UserRepository extends JpaRepository<User, String>{

}
