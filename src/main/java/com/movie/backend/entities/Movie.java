package com.movie.backend.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Movie {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;        
private String name;
private String genre;
private String language;
private int duration;
private double price;
private String description;
private LocalDate releaseDate; 
private String posterUrl;
private String bannerUrl;
private double rating;
private boolean featured;
}
