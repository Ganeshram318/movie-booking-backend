package com.movie.backend.Service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.movie.backend.Service.TheatreService;
import com.movie.backend.dto.TheatreRequestDto;
import com.movie.backend.dto.TheatreResponseDto;
import com.movie.backend.entities.Theatre;
import com.movie.backend.repository.TheatreRepository;
@Service
public class TheatreServiceImpl implements TheatreService {
	@Autowired
	private TheatreRepository theatreRepository;
	
	@Autowired
	private ModelMapper modelMapper;

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

	    Theatre theatre = theatreRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Theatre not found"));

	    theatre.setName(dto.getName());
	    theatre.setCity(dto.getCity());
	    theatre.setAddress(dto.getAddress());
	    theatre.setTotalscreens(dto.getTotalscreens());

	    Theatre updatedTheatre = theatreRepository.save(theatre);

	    return modelMapper.map(updatedTheatre, TheatreResponseDto.class);
	}
	
	@Override
	public void deleteTheatre(Integer id) {

	    Theatre theatre = theatreRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Theatre not found"));

	    theatreRepository.delete(theatre);
	}

}
