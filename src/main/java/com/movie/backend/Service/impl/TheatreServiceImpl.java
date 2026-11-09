package com.movie.backend.Service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.movie.backend.Service.TheatreService;
import com.movie.backend.dto.TheatreRequestDto;
import com.movie.backend.dto.TheatreResponseDto;
import com.movie.backend.entities.Theatre;
import com.movie.backend.repository.ShowtimeRepository;
import com.movie.backend.repository.TheatreRepository;
@Service
public class TheatreServiceImpl implements TheatreService {
	@Autowired
	private TheatreRepository theatreRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ShowtimeRepository showtimeRepository;

	@Override
	public TheatreResponseDto getTheatreById(Integer id) {
		return modelMapper.map(findTheatre(id), TheatreResponseDto.class);
	}

	private Theatre findTheatre(Integer id) {
		return theatreRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Theatre not found"));
	}

	@Override
	public TheatreResponseDto addTheatre(TheatreRequestDto dto) {

		 System.out.println("1. Request DTO: " + dto.getTotalscreens());

		    Theatre theatre = modelMapper.map(dto, Theatre.class);

		    System.out.println("2. Entity Before Save: " + theatre.getTotalscreens());

		    Theatre savedTheatre = theatreRepository.save(theatre);

		    System.out.println("3. Entity After Save: " + savedTheatre.getTotalscreens());

		    TheatreResponseDto response =
		            modelMapper.map(savedTheatre, TheatreResponseDto.class);

		    System.out.println("4. Response DTO: " + response.getTotalscreens());

		    return response;
	}

	@Override
	public List<TheatreResponseDto> getAllTheatres() {
		List<Theatre> theatres = theatreRepository.findAll();

        return theatres.stream().map(theatre -> modelMapper.map(theatre, TheatreResponseDto.class)).toList();
		
	}
	
	@Override
	public TheatreResponseDto updateTheatre(Integer id, TheatreRequestDto dto) {

	    Theatre theatre = findTheatre(id);

	    theatre.setName(dto.getName());
	    theatre.setCity(dto.getCity());
	    theatre.setAddress(dto.getAddress());
	    theatre.setTotalscreens(dto.getTotalscreens());

	    Theatre updatedTheatre = theatreRepository.save(theatre);

	    return modelMapper.map(updatedTheatre, TheatreResponseDto.class);
	}
	
	@Override
	public void deleteTheatre(Integer id) {

	    Theatre theatre = findTheatre(id);

	    if (showtimeRepository.existsByTheatreId(id)) {
	        throw new ResponseStatusException(HttpStatus.CONFLICT,
	                "This theatre has showtimes. Delete its showtimes before deleting the theatre.");
	    }

	    theatreRepository.delete(theatre);
	}

}
