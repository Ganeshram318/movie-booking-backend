package com.movie.backend.Service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.movie.backend.Service.UserService;
import com.movie.backend.dto.UserRequestDto;
import com.movie.backend.dto.UserResponseDto;
import com.movie.backend.entities.User;
import com.movie.backend.repository.UserRepository;
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserRepository userRepository;
public UserResponseDto register(UserRequestDto userRequestDto) {
	
	User user = modelMapper.map(userRequestDto, User.class);
	
	User savedUser = userRepository.save(user);
	
	UserResponseDto savedDto = modelMapper.map(savedUser,UserResponseDto.class);
	
	return savedDto;
	
}
}
