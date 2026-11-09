package com.movie.backend.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import com.movie.backend.entities.User;

import com.movie.backend.Service.UserService;
import com.movie.backend.dto.UserMeResponse;
import com.movie.backend.dto.UserRequestDto;
import com.movie.backend.dto.UserResponseDto;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
	@Autowired
	private UserService userService;
	@GetMapping("/profile")
	public ResponseEntity<String> profile(Authentication authentication) {

	    System.out.println("Authentication: " + authentication);

	    System.out.println("Principal: " + authentication.getPrincipal());

	    System.out.println("Name: " + authentication.getName());

	    return ResponseEntity.ok(
	        "Welcome " + authentication.getName()
	    );
	
	}
@PostMapping	
public ResponseEntity<UserResponseDto> register(@RequestBody UserRequestDto userRequestDto){
	UserResponseDto responseDto = userService.register(userRequestDto);
	
	return new ResponseEntity<UserResponseDto>(responseDto,HttpStatus.CREATED);
}
@GetMapping("/me")
public ResponseEntity<?> getCurrentUser() {

    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    User user = (User) authentication.getPrincipal();

    return ResponseEntity.ok(
        new UserMeResponse(
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole().name()
        )
    );
}
}
