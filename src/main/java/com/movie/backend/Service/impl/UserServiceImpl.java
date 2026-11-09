package com.movie.backend.Service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.movie.backend.Service.EmailService;
import com.movie.backend.Service.UserService;
import com.movie.backend.dto.UserRequestDto;
import com.movie.backend.dto.UserResponseDto;
import com.movie.backend.entities.User;
import com.movie.backend.enums.Role;
import com.movie.backend.repository.UserRepository;
@Service
public class UserServiceImpl implements UserService {
	
	private final BCryptPasswordEncoder passwordEncoder =
	        new BCryptPasswordEncoder();
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
public UserResponseDto register(UserRequestDto userRequestDto) {
	if (!userRequestDto.getPassword()
            .equals(userRequestDto.getConfirmPassword())) {

        throw new IllegalArgumentException(
                "Password and Confirm Password do not match");
    }

    User user = modelMapper.map(userRequestDto, User.class);
    
 // Always assign USER role during public registration
    user.setRole(Role.ROLE_USER);

    
    String hashedPassword =
            passwordEncoder.encode(userRequestDto.getPassword());

    user.setPassword(hashedPassword);
	    

    User savedUser = userRepository.save(user);

    emailService.sendRegistrationEmail(
            savedUser.getEmail(),
            savedUser.getFirstName()
    );

    return modelMapper.map(savedUser, UserResponseDto.class);
	
}

}
