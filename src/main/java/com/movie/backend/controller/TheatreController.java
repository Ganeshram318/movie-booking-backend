package com.movie.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.movie.backend.Service.TheatreService;
import com.movie.backend.dto.TheatreRequestDto;
import com.movie.backend.dto.TheatreResponseDto;

@RestController
@RequestMapping("/theatres")
@CrossOrigin
public class TheatreController {

    @Autowired
    private TheatreService theatreService;

    @PostMapping
    public ResponseEntity<TheatreResponseDto> addTheatre(
            @RequestBody TheatreRequestDto dto) {
    	
    	System.out.println("Total Screens Received: " + dto.getTotalscreens());

     TheatreResponseDto savedTheatre = theatreService.addTheatre(dto);

        return new ResponseEntity<>(savedTheatre, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TheatreResponseDto>> getAllTheatres() {

        List<TheatreResponseDto> theatres = theatreService.getAllTheatres();

        return ResponseEntity.ok(theatres);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TheatreResponseDto> updateTheatre(
            @PathVariable Integer id,
            @RequestBody TheatreRequestDto dto) {

        TheatreResponseDto updatedTheatre =
                theatreService.updateTheatre(id, dto);

        return ResponseEntity.ok(updatedTheatre);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheatre(@PathVariable Integer id) {

        theatreService.deleteTheatre(id);

        return ResponseEntity.noContent().build();
    }
}