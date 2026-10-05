package com.movie.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.backend.Service.UserService;
import com.movie.backend.dto.UserRequestDto;
import com.movie.backend.dto.UserResponseDto;

@RestController
@RequestMapping("/users")
public class UserController {
	@Autowired
	private UserService userService;
@PostMapping	
public ResponseEntity<UserResponseDto> register(@RequestBody UserRequestDto userRequestDto){
	UserResponseDto responseDto = userService.register(userRequestDto);
	
	return new ResponseEntity<UserResponseDto>(responseDto,HttpStatus.CREATED);
}
}
