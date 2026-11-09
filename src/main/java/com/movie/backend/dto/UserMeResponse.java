package com.movie.backend.dto;

public record UserMeResponse(
    String firstName,
    String lastName,
    String email,
    String role
) {}